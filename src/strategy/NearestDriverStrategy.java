package strategy;

import model.Driver;
import model.Rider;

import java.util.List;

/**
 * Matching strategy that selects the driver whose location name
 * is lexicographically nearest to the rider's location.
 *
 * In a real system this would use GPS coordinates; here we simulate
 * proximity by comparing the location strings alphabetically, which
 * guarantees deterministic, testable behaviour without external APIs.
 *
 * SRP : Contains only the nearest-driver selection algorithm.
 * LSP : Fully substitutable for any other RideMatchingStrategy.
 * OCP : Adding a new strategy never requires changing this class.
 */
public class NearestDriverStrategy implements RideMatchingStrategy {

    @Override
    public Driver findDriver(Rider rider, List<Driver> drivers) {
        if (drivers == null || drivers.isEmpty()) {
            return null;
        }

        Driver nearest = null;
        int bestScore = Integer.MAX_VALUE;

        for (Driver driver : drivers) {
            // Simulate proximity: count matching leading characters in location names
            int score = locationDifference(rider.getLocation(), driver.getCurrentLocation());
            if (score < bestScore) {
                bestScore = score;
                nearest = driver;
            }
        }

        return nearest;
    }

    /**
     * Simple heuristic: the fewer characters the two location strings share
     * as a prefix, the "farther" they are.  Returns the number of differing
     * characters after the longest common prefix.
     */
    private int locationDifference(String loc1, String loc2) {
        String l1 = loc1.toLowerCase();
        String l2 = loc2.toLowerCase();
        int commonLength = 0;
        int minLen = Math.min(l1.length(), l2.length());

        for (int i = 0; i < minLen; i++) {
            if (l1.charAt(i) == l2.charAt(i)) {
                commonLength++;
            } else {
                break;
            }
        }
        // Higher common prefix → more similar location → lower score (closer)
        return (l1.length() + l2.length()) - 2 * commonLength;
    }
}
