package org.example.reservationservice.state;

import org.example.reservationservice.entitites.ReservationStatus;

public class ReservationStateFactory {

    public static ReservationState getState(ReservationStatus status) {
        if (status == null) return new ConfirmedState();
        
        switch (status) {
            case CONFIRMED:
                return new ConfirmedState();
            case CANCELLED:
                return new CancelledState();
            case COMPLETED:
                return new CompletedState();
            default:
                throw new IllegalArgumentException("Unknown status");
        }
    }
}
