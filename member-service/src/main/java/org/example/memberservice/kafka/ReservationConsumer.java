package org.example.memberservice.kafka;

import org.example.memberservice.entitites.Member;
import org.example.memberservice.services.MemberService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ReservationConsumer {

    private final MemberService memberService;
    
    // Simule en mémoire le nombre de réservations d'un membre
    private final Map<Long, Integer> activeReservationsCount = new HashMap<>();

    public ReservationConsumer(MemberService memberService) {
        this.memberService = memberService;
    }

    @KafkaListener(topics = "reservation-created-topic", groupId = "member-service-group")
    public void consumeReservationCreatedEvent(Long memberId) {
        Member member = memberService.getMemberById(memberId);
        
        int count = activeReservationsCount.getOrDefault(memberId, 0) + 1;
        activeReservationsCount.put(memberId, count);

        // si le quota est atteint, on suspend le membre
        if (count >= member.getMaxConcurrentBookings()) {
            memberService.updateMemberStatus(memberId, true);
            System.out.println("Membre " + memberId + " suspendu (Quota max atteint : " + count + ")");
        }
    }

    @KafkaListener(topics = "reservation-ended-topic", groupId = "member-service-group")
    public void consumeReservationEndedEvent(Long memberId) {
        Member member = memberService.getMemberById(memberId);
        
        int count = activeReservationsCount.getOrDefault(memberId, 1) - 1;
        if(count < 0) count = 0;
        
        activeReservationsCount.put(memberId, count);

        // Rsi on repasse sous le quota et que le membre était suspendu, on le désuspend
        if (count < member.getMaxConcurrentBookings() && member.isSuspended()) {
            memberService.updateMemberStatus(memberId, false);
            System.out.println("Membre " + memberId + " réactivé (Sous le quota : " + count + ")");
        }
    }
}
