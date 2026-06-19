package strategy;

import model.Ride;

import java.time.LocalTime;

/**
 * Peak-hour fare calculation applies a surge multiplier during busy hours.
 *
 * Peak hours  : 08:00–10:00 and 17:00–20:00
 * Base fare   : Rs. 30
 * Per km rate : Rs. 10
 * Surge factor: 1.5× during peak hours, 1.0× otherwise
 *
 * SRP : Contains only the peak-hour pricing algorithm.
 * LSP : Fully substitutable for any other FareStrategy.
 * OCP : DefaultFareStrategy is untouched; we simply add this new class.
 */
public class PeakHourFareStrategy implements FareStrategy {

    private static final double BASE_FARE      = 30.0;
    private static final double RATE_PER_KM    = 10.0;
    private static final double SURGE_MULTIPLIER = 1.5;

    // Morning peak: 08:00 – 10:00
    private static final LocalTime MORNING_START = LocalTime.of(8, 0);
    private static final LocalTime MORNING_END   = LocalTime.of(10, 0);

    // Evening peak: 17:00 – 20:00
    private static final LocalTime EVENING_START = LocalTime.of(17, 0);
    private static final LocalTime EVENING_END   = LocalTime.of(20, 0);

    @Override
    public double calculateFare(Ride ride) {
        double baseFare = BASE_FARE + (ride.getDistance() * RATE_PER_KM);
        double multiplier = isPeakHour() ? SURGE_MULTIPLIER : 1.0;
        return baseFare * multiplier;
    }

    /**
     * Checks whether the current system time falls within a defined peak window.
     */
    private boolean isPeakHour() {
        LocalTime now = LocalTime.now();
        boolean morningPeak = !now.isBefore(MORNING_START) && now.isBefore(MORNING_END);
        boolean eveningPeak = !now.isBefore(EVENING_START) && now.isBefore(EVENING_END);
        return morningPeak || eveningPeak;
    }
}
