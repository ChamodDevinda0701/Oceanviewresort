package oceanviewresort.model;

import java.util.Date;

public class Reservation {

    private String reservationNumber;
    private String guestName;
    private String address;
    private String contactNumber;
    private String roomNumber;
    private String roomType;
    private Date checkInDate;
    private Date checkOutDate;
    private double totalAmount;

    public Reservation() {}

    public Reservation(String reservationNumber, String guestName,
                       String address, String contactNumber,
                       String roomNumber, String roomType,
                       Date checkInDate, Date checkOutDate,
                       double totalAmount) {
        this.reservationNumber = reservationNumber;
        this.guestName = guestName;
        this.address = address;
        this.contactNumber = contactNumber;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.totalAmount = totalAmount;
    }

    public String getReservationNumber() { return reservationNumber; }
    public void setReservationNumber(String n) { this.reservationNumber = n; }

    public String getGuestName() { return guestName; }
    public void setGuestName(String n) { this.guestName = n; }

    public String getAddress() { return address; }
    public void setAddress(String a) { this.address = a; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String c) { this.contactNumber = c; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String r) { this.roomNumber = r; }

    public String getRoomType() { return roomType; }
    public void setRoomType(String t) { this.roomType = t; }

    public Date getCheckInDate() { return checkInDate; }
    public void setCheckInDate(Date d) { this.checkInDate = d; }

    public Date getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(Date d) { this.checkOutDate = d; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double t) { this.totalAmount = t; }

    public long getNumberOfNights() {
        if (checkInDate == null || checkOutDate == null) return 0;
        long diff = checkOutDate.getTime() - checkInDate.getTime();
        return diff / (1000 * 60 * 60 * 24);
    }
}
