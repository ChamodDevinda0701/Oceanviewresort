package oceanviewresort.factory;

import oceanviewresort.model.Room;

public class DeluxeRoom extends Room {

    public DeluxeRoom(String roomNumber) {
        super(roomNumber, "Deluxe", 9500.00, true);
    }

    public String getAmenities() {
        return "WiFi, Smart TV, Air Conditioning, Mini Bar, " +
               "Hot Water, Sea View Balcony";
    }

    public String getDescription() {
        return "Spacious deluxe room with stunning sea view. " +
               "Perfect for couples and families.";
    }
}
