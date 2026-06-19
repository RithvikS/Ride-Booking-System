package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents the fare receipt generated after a ride is completed.
 * SRP: Only holds receipt data, no business logic.
 */
public class FareReceipt {

    private final String rideId;
    private final double amount;
    private final LocalDateTime generatedAt;

    public FareReceipt(String rideId, double amount) {
        this.rideId = rideId;
        this.amount = amount;
        this.generatedAt = LocalDateTime.now();
    }

    // ---------- Getters ----------

    public String getRideId() {
        return rideId;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format(
            "╔══════════════════════════════════╗%n" +
            "║         FARE RECEIPT             ║%n" +
            "╠══════════════════════════════════╣%n" +
            "║ Ride ID   : %-20s ║%n" +
            "║ Amount    : Rs. %-17.2f ║%n" +
            "║ Generated : %-20s ║%n" +
            "╚══════════════════════════════════╝",
            rideId, amount, formatter.format(generatedAt)
        );
    }
}
