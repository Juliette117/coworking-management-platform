package org.example.reservationservice.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ReservationProducer {

    private final KafkaTemplate<String, Long> kafkaTemplate;

    public ReservationProducer(KafkaTemplate<String, Long> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // Informe le Member Service qu'une réservation a été créée
    public void sendReservationCreatedEvent(Long memberId) {
        kafkaTemplate.send("reservation-created-topic", memberId);
    }

    // Informe le Member Service qu'une réservation a été annulée ou complétée
    public void sendReservationEndedEvent(Long memberId) {
        kafkaTemplate.send("reservation-ended-topic", memberId);
    }
}
