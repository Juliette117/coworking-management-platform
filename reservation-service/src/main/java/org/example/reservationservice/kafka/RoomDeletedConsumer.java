package org.example.reservationservice.kafka;

import org.example.reservationservice.entitites.Reservation;
import org.example.reservationservice.entitites.ReservationStatus;
import org.example.reservationservice.repositories.ReservationRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomDeletedConsumer {

    private final ReservationRepository reservationRepository;
    private final ReservationProducer reservationProducer;

    public RoomDeletedConsumer(ReservationRepository reservationRepository, ReservationProducer reservationProducer) {
        this.reservationRepository = reservationRepository;
        this.reservationProducer = reservationProducer;
    }

    @KafkaListener(topics = "room-deleted-topic", groupId = "reservation-service-group")
    public void consumeRoomDeletedEvent(Long roomId) {
        System.out.println("Salle " + roomId + " supprimée. Annulation des réservations en cascade...");
        
        List<Reservation> reservations = reservationRepository.findByRoomId(roomId);
        
        for (Reservation reservation : reservations) {
            if (reservation.getStatus() == ReservationStatus.CONFIRMED) {
                reservation.setStatus(ReservationStatus.CANCELLED);
                reservationRepository.save(reservation);
                
                // Prévenir le MemberService que la réservation a été annulée
                reservationProducer.sendReservationEndedEvent(reservation.getMemberId());
            }
        }
    }
}
