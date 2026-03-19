package org.example.reservationservice.kafka;

import org.example.reservationservice.entitites.Reservation;
import org.example.reservationservice.entitites.ReservationStatus;
import org.example.reservationservice.repositories.ReservationRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberDeletedConsumer {

    private final ReservationRepository reservationRepository;
    private final ReservationProducer reservationProducer;

    public MemberDeletedConsumer(ReservationRepository reservationRepository, ReservationProducer reservationProducer) {
        this.reservationRepository = reservationRepository;
        this.reservationProducer = reservationProducer;
    }

    @KafkaListener(topics = "member-deleted-topic", groupId = "reservation-service-group")
    public void consumeMemberDeletedEvent(Long memberId) {
        System.out.println("Membre " + memberId + " supprimé. Annulation des réservations associées...");
        
        List<Reservation> reservations = reservationRepository.findByMemberId(memberId);
        
        for (Reservation reservation : reservations) {
            if (reservation.getStatus() == ReservationStatus.CONFIRMED) {
                reservation.setStatus(ReservationStatus.CANCELLED);
                reservationRepository.save(reservation);
                
                // Prévenir le MemberService (qui ne recevra plus ce message vu qu'il est supprimé, 
                // mais c'est une bonne pratique de garder le flux d'événements cohérent).
                // Si on a un historique, ou un service d'analytics qui écoute, il sera notifié.
                reservationProducer.sendReservationEndedEvent(memberId);
            }
        }
    }
}