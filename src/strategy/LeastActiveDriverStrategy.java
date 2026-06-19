package strategy;

import model.Driver;
import model.Rider;

import java.util.List;

/**
 * Matching strategy that selects the driver with the fewest completed rides,
 * ensuring fair work distribution across all drivers.
 *
 * SRP : Contains only the least-active selection algorithm.
 * LSP : Fully substitutable for any other RideMatchingStrategy.
 * OCP : Adding a new strategy never requires changing this class.
 */
public class LeastActiveDriverStrategy implements RideMatchingStrategy {

    @Override
    public Driver findDriver(Rider rider, List<Driver> drivers) {
        if (drivers == null || drivers.isEmpty()) {
            return null;
        }

        Driver leastActive = null;
        int minRides = Integer.MAX_VALUE;

        for (Driver driver : drivers) {
            if (driver.getTotalRidesCompleted() < minRides) {
                minRides = driver.getTotalRidesCompleted();
                leastActive = driver;
            }
        }

        return leastActive;
    }
}
