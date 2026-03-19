package org.example.reservationservice.state;

import org.example.reservationservice.entitites.Reservation;
import org.example.reservationservice.entitites.ReservationStatus;

public class ConfirmedState implements ReservationState {

    @Override
    public void cancel(Reservation reservation) {
        reservation.setStatus(ReservationStatus.CANCELLED);
    }

    @Override
    public void complete(Reservation reservation) {
        reservation.setStatus(ReservationStatus.COMPLETED);
    }
}
