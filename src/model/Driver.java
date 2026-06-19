package model;

/**
 * Represents a driver who provides rides on the platform.
 * SRP: Only holds driver data, no business logic.
 */
public class Driver {

    private final String id;
    private String name;
    private String currentLocation;
    private boolean available;
    private int totalRidesCompleted; // used by LeastActiveDriverStrategy

    public Driver(String id, String name, String currentLocation) {
        this.id = id;
        this.name = name;
        this.currentLocation = currentLocation;
        this.available = true;        // drivers start as available
        this.totalRidesCompleted = 0;
    }

    // ---------- Getters & Setters ----------

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public int getTotalRidesCompleted() {
        return totalRidesCompleted;
    }

    public void incrementRidesCompleted() {
        this.totalRidesCompleted++;
    }

    @Override
    public String toString() {
        return String.format(
            "Driver[id=%s, name=%s, location=%s, available=%s, ridesCompleted=%d]",
            id, name, currentLocation, available, totalRidesCompleted
        );
    }
}
