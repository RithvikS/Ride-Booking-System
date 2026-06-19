package exception;

/**
 * Thrown when a driver with the given ID is not found in the repository.
 */
public class DriverNotFoundException extends RuntimeException {
    public DriverNotFoundException(String driverId) {
        super("Driver not found with ID: " + driverId);
    }
}
