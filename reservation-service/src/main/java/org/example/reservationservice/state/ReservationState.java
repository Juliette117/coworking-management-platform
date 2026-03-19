package org.example.reservationservice.state;

import org.example.reservationservice.entitites.Reservation;

public interface ReservationState {
    void cancel(Reservation reservation);
    void complete(Reservation reservation);
}
