package org.example.roomservice.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.math.BigDecimal;

@Entity
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String city;
    private Integer capacity;
    private RoomType type; // OPEN_SPACE, MEETING_ROOM, PRIVATE_OFFICE
    private BigDecimal hourlyRate;
    private boolean available;
}