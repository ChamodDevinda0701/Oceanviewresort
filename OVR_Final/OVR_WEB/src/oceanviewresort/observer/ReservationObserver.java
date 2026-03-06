package oceanviewresort.observer;

import oceanviewresort.model.Reservation;

public interface ReservationObserver {
    void onReservationAdded(Reservation reservation);
    void onReservationCancelled(String reservationNumber);
}
