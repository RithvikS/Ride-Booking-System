package exception;

/**
 * Thrown when a rider with the given ID is not found in the repository.
 */
public class RiderNotFoundException extends RuntimeException {
    public RiderNotFoundException(String riderId) {
        super("Rider not found with ID: " + riderId);
    }
}
