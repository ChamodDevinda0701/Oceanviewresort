package oceanviewresort.factory;

import oceanviewresort.model.Room;

public class StandardRoom extends Room {

    public StandardRoom(String roomNumber) {
        super(roomNumber, "Standard", 5000.00, true);
    }

    public String getAmenities() {
        return "WiFi, TV, Air Conditioning, Hot Water";
    }

    public String getDescription() {
        return "Comfortable standard room with garden view. " +
               "Ideal for budget-conscious travelers.";
    }
}
