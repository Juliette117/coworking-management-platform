package org.example.reservationservice.services;

import org.example.reservationservice.entitites.Reservation;
import org.example.reservationservice.entitites.ReservationStatus;
import org.example.reservationservice.dto.MemberDTO;
import org.example.reservationservice.controllers.restClients.MemberRestClient;
import org.example.reservationservice.dto.RoomDTO;
import org.example.reservationservice.controllers.restClients.RoomRestClient;
import org.example.reservationservice.kafka.ReservationProducer;
import org.example.reservationservice.state.ReservationState;
import org.example.reservationservice.state.ReservationStateFactory;
import org.example.reservationservice.repositories.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomRestClient roomRestClient;
    private final MemberRestClient memberRestClient;
    private final ReservationProducer reservationProducer;

    public ReservationService(ReservationRepository reservationRepository, 
                              RoomRestClient roomRestClient, 
                              MemberRestClient memberRestClient,
                              ReservationProducer reservationProducer) {
        this.reservationRepository = reservationRepository;
        this.roomRestClient = roomRestClient;
        this.memberRestClient = memberRestClient;
        this.reservationProducer = reservationProducer;
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Reservation getReservationById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
    }

    @Transactional
    public Reservation createReservation(Reservation reservationRequest) {
        //Vérifier la salle
        RoomDTO room;
        try {
            room = roomRestClient.getRoomById(reservationRequest.getRoomId());
        } catch (Exception e) {
            throw new RuntimeException("Room not found or Room Service unavailable");
        }

        if (!room.isAvailable()) {
            throw new RuntimeException("Room is not available");
        }

        //Vérifier le membre
        MemberDTO member;
        try {
            member = memberRestClient.getMemberById(reservationRequest.getMemberId());
        } catch (Exception e) {
            throw new RuntimeException("Member not found or Member Service unavailable");
        }

        if (member.isSuspended()) {
            throw new RuntimeException("Member is suspended and cannot make a reservation");
        }

        //Créer la réservation à l'aide du Builder
        Reservation reservation = new Reservation.Builder()
                .withRoom(reservationRequest.getRoomId())
                .withMember(reservationRequest.getMemberId())
                .withTimeFrame(
                        reservationRequest.getStartDateTime() != null ? reservationRequest.getStartDateTime() : LocalDateTime.now(),
                        reservationRequest.getEndDateTime()
                )
                .withStatus(ReservationStatus.CONFIRMED)
                .build();

        Reservation savedReservation = reservationRepository.save(reservation);

        // Mettre à jour la disponibilité de la salle
        roomRestClient.updateRoomAvailability(reservation.getRoomId(), false);

        //Envoyer event pour mettre à jour le nombre de réservations actives du membre
        reservationProducer.sendReservationCreatedEvent(reservation.getMemberId());

        return savedReservation;
    }

    @Transactional
    public Reservation cancelReservation(Long id) {
        Reservation reservation = getReservationById(id);
        
        // Utilisation du pattern STATE
        ReservationState state = ReservationStateFactory.getState(reservation.getStatus());
        state.cancel(reservation); // Lèvera une exception si l'état actuel ne permet pas l'annulation

        Reservation savedReservation = reservationRepository.save(reservation);

        // Rendre la salle à nouveau disponible
        roomRestClient.updateRoomAvailability(reservation.getRoomId(), true);

        //Envoyer event pour diminuer le nombre de réservations du membre
        reservationProducer.sendReservationEndedEvent(reservation.getMemberId());

        return savedReservation;
    }

    @Transactional
    public Reservation completeReservation(Long id) {
        Reservation reservation = getReservationById(id);

        // Utilisation du pattern STATE
        ReservationState state = ReservationStateFactory.getState(reservation.getStatus());
        state.complete(reservation); // Lèvera une exception si l'état actuel ne permet pas la complétion

        Reservation savedReservation = reservationRepository.save(reservation);

        // Rendre la salle à nouveau disponible
        roomRestClient.updateRoomAvailability(reservation.getRoomId(), true);

        //Envoyer event pour diminuer le nombre de réservations du membre
        reservationProducer.sendReservationEndedEvent(reservation.getMemberId());

        return savedReservation;
    }
}