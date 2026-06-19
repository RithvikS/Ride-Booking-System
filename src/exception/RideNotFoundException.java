package exception;

/**
 * Thrown when a ride with the given ID is not found in the repository.
 */
public class RideNotFoundException extends RuntimeException {
    public RideNotFoundException(String rideId) {
        super("Ride not found with ID: " + rideId);
    }
}
