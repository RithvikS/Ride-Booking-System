package exception;

/**
 * Thrown when no available driver can be matched for a ride request.
 */
public class NoDriverAvailableException extends RuntimeException {
    public NoDriverAvailableException() {
        super("No driver is currently available. Please try again later.");
    }
}
