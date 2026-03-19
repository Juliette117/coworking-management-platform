package org.example.memberservice.services;

import org.example.memberservice.entitites.Member;
import org.example.memberservice.kafka.MemberProducer;
import org.example.memberservice.repositories.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberProducer memberProducer;

    public MemberService(MemberRepository memberRepository, MemberProducer memberProducer) {
        this.memberRepository = memberRepository;
        this.memberProducer = memberProducer;
    }

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public Member getMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found"));
    }

    public Member createMember(Member member) {
        return memberRepository.save(member);
    }

    public Member updateMember(Long id, Member updatedMember) {
        Member existingMember = getMemberById(id);
        existingMember.setFullName(updatedMember.getFullName());
        existingMember.setEmail(updatedMember.getEmail());
        existingMember.setSubscriptionType(updatedMember.getSubscriptionType());
        existingMember.setSuspended(updatedMember.isSuspended());
        return memberRepository.save(existingMember);
    }

    public void deleteMember(Long id) {
        Member member = getMemberById(id);
        memberRepository.delete(member);
        
        // Envoi d'un événement Kafka pour notifier de la suppression du membre
        memberProducer.sendMemberDeletedEvent(id);
    }

    // Méthode pour suspendre / désuspendre un membre
    public void updateMemberStatus(Long id, boolean isSuspended) {
        Member member = getMemberById(id);
        member.setSuspended(isSuspended);
        memberRepository.save(member);
    }
}