package strategy;

import model.Ride;

/**
 * Strategy interface for calculating the fare of a completed ride.
 *
 * OCP : New pricing models are added by implementing this interface,
 *       without touching existing fare strategies.
 * ISP : Small, single-method interface — no unnecessary methods.
 * DIP : RideService depends on this abstraction, never on concrete classes.
 */
public interface FareStrategy {

    /**
     * Calculates the total fare for the given ride.
     *
     * @param ride the completed ride
     * @return the calculated fare amount in rupees
     */
    double calculateFare(Ride ride);
}
