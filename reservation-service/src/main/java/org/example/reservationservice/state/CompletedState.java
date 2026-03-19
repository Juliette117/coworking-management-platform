package org.example.reservationservice.state;

import org.example.reservationservice.entitites.Reservation;

public class CompletedState implements ReservationState {

    @Override
    public void cancel(Reservation reservation) {
        throw new IllegalStateException("Cannot cancel a completed reservation");
    }

    @Override
    public void complete(Reservation reservation) {
        throw new IllegalStateException("Reservation is already completed");
    }
}
