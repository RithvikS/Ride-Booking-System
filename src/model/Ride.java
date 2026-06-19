package model;

/**
 * Represents a ride booking entity.
 * SRP: Only holds ride data, no business logic.
 */
public class Ride {

    private final String id;
    private final Rider rider;
    private Driver driver;
    private final double distance;   // in kilometres
    private RideStatus status;

    public Ride(String id, Rider rider, double distance) {
        this.id = id;
        this.rider = rider;
        this.distance = distance;
        this.status = RideStatus.REQUESTED;
    }

    // ---------- Getters & Setters ----------

    public String getId() {
        return id;
    }

    public Rider getRider() {
        return rider;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public double getDistance() {
        return distance;
    }

    public RideStatus getStatus() {
        return status;
    }

    public void setStatus(RideStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        String driverInfo = (driver != null) ? driver.getName() : "Not Assigned";
        return String.format(
            "Ride[id=%s, rider=%s, driver=%s, distance=%.1f km, status=%s]",
            id, rider.getName(), driverInfo, distance, status
        );
    }
}
