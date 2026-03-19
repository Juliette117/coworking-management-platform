package org.example.reservationservice.entitites;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long roomId;
    private Long memberId;

    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status; // CONFIRMED, CANCELLED, COMPLETED

    public Reservation() {
    }

    // Constructor utilisé par le Builder
    private Reservation(Builder builder) {
        this.roomId = builder.roomId;
        this.memberId = builder.memberId;
        this.startDateTime = builder.startDateTime;
        this.endDateTime = builder.endDateTime;
        this.status = builder.status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }

    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }

    public LocalDateTime getStartDateTime() { return startDateTime; }
    public void setStartDateTime(LocalDateTime startDateTime) { this.startDateTime = startDateTime; }

    public LocalDateTime getEndDateTime() { return endDateTime; }
    public void setEndDateTime(LocalDateTime endDateTime) { this.endDateTime = endDateTime; }

    public ReservationStatus getStatus() { return status; }
    public void setStatus(ReservationStatus status) { this.status = status; }

    // --- Implémentation du pattern Builder ---
    public static class Builder {
        private Long roomId;
        private Long memberId;
        private LocalDateTime startDateTime;
        private LocalDateTime endDateTime;
        private ReservationStatus status = ReservationStatus.CONFIRMED; // Par défaut

        public Builder withRoom(Long roomId) {
            this.roomId = roomId;
            return this;
        }

        public Builder withMember(Long memberId) {
            this.memberId = memberId;
            return this;
        }

        public Builder withTimeFrame(LocalDateTime start, LocalDateTime end) {
            this.startDateTime = start;
            this.endDateTime = end;
            return this;
        }

        public Builder withStatus(ReservationStatus status) {
            this.status = status;
            return this;
        }

        public Reservation build() {
            // Validations basiques à la construction
            if (roomId == null) throw new IllegalArgumentException("RoomId is required");
            if (memberId == null) throw new IllegalArgumentException("MemberId is required");
            if (startDateTime == null) this.startDateTime = LocalDateTime.now();
            
            return new Reservation(this);
        }
    }
}
