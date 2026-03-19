package org.example.roomservice.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class RoomProducer {

    private final KafkaTemplate<String, Long> kafkaTemplate;

    public RoomProducer(KafkaTemplate<String, Long> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // Informe les autres services qu'une salle a été supprimée
    public void sendRoomDeletedEvent(Long roomId) {
        kafkaTemplate.send("room-deleted-topic", roomId);
    }
}
