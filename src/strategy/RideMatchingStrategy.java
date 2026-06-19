package strategy;

import model.Driver;
import model.Rider;

import java.util.List;

/**
 * Strategy interface for matching a rider with an available driver.
 *
 * OCP: New matching algorithms can be added by implementing this interface
 *      without modifying existing code.
 * ISP: This interface has only the method relevant to matching — small and focused.
 * DIP: RideService depends on this abstraction, not concrete implementations.
 */
public interface RideMatchingStrategy {

    /**
     * Finds the most suitable available driver for the given rider.
     *
     * @param rider   the rider requesting a ride
     * @param drivers list of all available drivers
     * @return the selected Driver, or null if none found
     */
    Driver findDriver(Rider rider, List<Driver> drivers);
}
