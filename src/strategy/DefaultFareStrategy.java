package strategy;

import model.Ride;

/**
 * Default fare calculation: flat base fare + per-km rate.
 *
 * Base fare  : Rs. 30
 * Per km rate: Rs. 10
 *
 * SRP : Contains only the default pricing algorithm.
 * LSP : Fully substitutable for any other FareStrategy.
 */
public class DefaultFareStrategy implements FareStrategy {

    private static final double BASE_FARE    = 30.0;
    private static final double RATE_PER_KM  = 10.0;

    @Override
    public double calculateFare(Ride ride) {
        // Total fare = base fare + (distance × per-km rate)
        return BASE_FARE + (ride.getDistance() * RATE_PER_KM);
    }
}
