package org.example.memberservice.entitites;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fullName;
    private String email;
    private SubscriptionType subscriptionType; // BASIC, PRO, ENTERPRISE
    private boolean suspended;
    private Integer maxConcurrentBookings;
}