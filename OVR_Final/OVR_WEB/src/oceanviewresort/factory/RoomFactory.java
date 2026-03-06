package oceanviewresort.factory;

import oceanviewresort.model.Room;

public class RoomFactory {

    public static final String STANDARD = "Standard";
    public static final String DELUXE   = "Deluxe";
    public static final String SUITE    = "Suite";

    // Factory Pattern - centralised object creation
    public static Room createRoom(String roomNumber, String roomType) {
        switch (roomType) {
            case STANDARD:
                return new StandardRoom(roomNumber);
            case DELUXE:
                return new DeluxeRoom(roomNumber);
            case SUITE:
                return new SuiteRoom(roomNumber);
            default:
                throw new IllegalArgumentException(
                    "Unknown room type: " + roomType);
        }
    }

    public static double getPriceForType(String roomType) {
        switch (roomType) {
            case STANDARD: return 5000.00;
            case DELUXE:   return 9500.00;
            case SUITE:    return 18000.00;
            default:       return 0.0;
        }
    }

    public static String getDescriptionForType(String roomType) {
        switch (roomType) {
            case STANDARD:
                return "Comfortable room with garden view. " +
                       "Amenities: WiFi, TV, AC, Hot Water.";
            case DELUXE:
                return "Spacious room with sea view balcony. " +
                       "Amenities: WiFi, Smart TV, AC, Mini Bar.";
            case SUITE:
                return "Luxury suite with panoramic ocean view. " +
                       "Amenities: WiFi, Jacuzzi, Butler, Breakfast.";
            default:
                return "Room information not available.";
        }
    }
}
