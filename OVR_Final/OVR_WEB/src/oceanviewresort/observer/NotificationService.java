package oceanviewresort.observer;

import oceanviewresort.model.Reservation;
import java.util.ArrayList;
import java.util.List;

public class NotificationService implements ReservationSubject {

    private static NotificationService instance;
    private List<ReservationObserver> observers;
    private List<String> notificationLog;

    private NotificationService() {
        observers = new ArrayList<>();
        notificationLog = new ArrayList<>();
    }

    public static NotificationService getInstance() {
        if (instance == null) {
            instance = new NotificationService();
        }
        return instance;
    }

    @Override
    public void addObserver(ReservationObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void removeObserver(ReservationObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        // General purpose - used by subclasses if needed
    }

    public void notifyReservationAdded(Reservation reservation) {
        String msg = "[NEW BOOKING] " +
                     reservation.getReservationNumber() +
                     " | Guest: " + reservation.getGuestName() +
                     " | Room: " + reservation.getRoomNumber() +
                     " (" + reservation.getRoomType() + ")";
        notificationLog.add(msg);
        System.out.println(msg);
        for (ReservationObserver observer : observers) {
            observer.onReservationAdded(reservation);
        }
    }

    public void notifyReservationCancelled(String reservationNumber) {
        String msg = "[CANCELLED] Reservation " +
                     reservationNumber + " has been cancelled.";
        notificationLog.add(msg);
        System.out.println(msg);
        for (ReservationObserver observer : observers) {
            observer.onReservationCancelled(reservationNumber);
        }
    }

    public List<String> getNotificationLog() {
        return new ArrayList<>(notificationLog);
    }

    public void clearLog() {
        notificationLog.clear();
    }
}
