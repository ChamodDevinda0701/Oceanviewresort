package oceanviewresort.factory;

import oceanviewresort.model.Room;

public class SuiteRoom extends Room {

    public SuiteRoom(String roomNumber) {
        super(roomNumber, "Suite", 18000.00, true);
    }

    public String getAmenities() {
        return "WiFi, Smart TV, Air Conditioning, Mini Bar, " +
               "Jacuzzi, Private Balcony, Butler Service, " +
               "Complimentary Breakfast";
    }

    public String getDescription() {
        return "Luxurious suite with panoramic ocean view. " +
               "The ultimate beachside experience.";
    }
}
