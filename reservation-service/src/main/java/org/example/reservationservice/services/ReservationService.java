package org.example.reservationservice.services;

import org.example.reservationservice.entitites.Reservation;
import org.example.reservationservice.entitites.ReservationStatus;
import org.example.reservationservice.feign.MemberDTO;
import org.example.reservationservice.feign.MemberRestClient;
import org.example.reservationservice.feign.RoomDTO;
import org.example.reservationservice.feign.RoomRestClient;
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

    public ReservationService(ReservationRepository reservationRepository, 
                              RoomRestClient roomRestClient, 
                              MemberRestClient memberRestClient) {
        this.reservationRepository = reservationRepository;
        this.roomRestClient = roomRestClient;
        this.memberRestClient = memberRestClient;
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Reservation getReservationById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
    }

    @Transactional
    public Reservation createReservation(Reservation reservation) {
        // Vérifier que la salle existe et est disponible
        RoomDTO room;
        try {
            room = roomRestClient.getRoomById(reservation.getRoomId());
        } catch (Exception e) {
            throw new RuntimeException("Room not found or Room Service unavailable");
        }

        if (!room.isAvailable()) {
            throw new RuntimeException("Room is not available");
        }

        // Vérifier que le membre existe et n'est pas suspendu
        MemberDTO member;
        try {
            member = memberRestClient.getMemberById(reservation.getMemberId());
        } catch (Exception e) {
            throw new RuntimeException("Member not found or Member Service unavailable");
        }

        if (member.isSuspended()) {
            throw new RuntimeException("Member is suspended and cannot make a reservation");
        }

        //  Créer la réservation
        reservation.setStatus(ReservationStatus.CONFIRMED);
        
        // S'assurer d'avoir des dates
        if(reservation.getStartDateTime() == null) {
            reservation.setStartDateTime(LocalDateTime.now());
        }

        Reservation savedReservation = reservationRepository.save(reservation);

        // 4. Mettre à jour la disponibilité de la salle via Feign Client
        roomRestClient.updateRoomAvailability(reservation.getRoomId(), false);

        // TODO: Kafka -> Envoyer event pour mettre à jour le nombre de réservations actives du membre

        return savedReservation;
    }

    @Transactional
    public Reservation cancelReservation(Long id) {
        Reservation reservation = getReservationById(id);
        
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new RuntimeException("Reservation is already cancelled");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        Reservation savedReservation = reservationRepository.save(reservation);

        // Rendre la salle à nouveau disponible
        roomRestClient.updateRoomAvailability(reservation.getRoomId(), true);

        // TODO: Kafka -> Envoyer event pour diminuer le nombre de réservations du membre

        return savedReservation;
    }

    @Transactional
    public Reservation completeReservation(Long id) {
        Reservation reservation = getReservationById(id);

        if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
            throw new RuntimeException("Only CONFIRMED reservations can be completed");
        }

        reservation.setStatus(ReservationStatus.COMPLETED);
        Reservation savedReservation = reservationRepository.save(reservation);

        // Rendre la salle à nouveau disponible
        roomRestClient.updateRoomAvailability(reservation.getRoomId(), true);

        // TODO: Kafka -> Envoyer event pour diminuer le nombre de réservations du membre

        return savedReservation;
    }
}
