package org.example.reservationservice.state;

import org.example.reservationservice.entitites.Reservation;

public class CancelledState implements ReservationState {

    @Override
    public void cancel(Reservation reservation) {
        throw new IllegalStateException("Reservation is already cancelled");
    }

    @Override
    public void complete(Reservation reservation) {
        throw new IllegalStateException("Cannot complete a cancelled reservation");
    }
}
