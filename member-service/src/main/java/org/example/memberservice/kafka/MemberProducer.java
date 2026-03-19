package org.example.memberservice.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class MemberProducer {

    private final KafkaTemplate<String, Long> kafkaTemplate;

    public MemberProducer(KafkaTemplate<String, Long> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // Informe le Reservation Service qu'un membre a été supprimé
    public void sendMemberDeletedEvent(Long memberId) {
        kafkaTemplate.send("member-deleted-topic", memberId);
    }
}