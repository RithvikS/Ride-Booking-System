package service;

import exception.NoDriverAvailableException;
import exception.RideNotFoundException;
import model.Driver;
import model.FareReceipt;
import model.Ride;
import model.RideStatus;
import repository.RideRepository;
import strategy.FareStrategy;
import strategy.RideMatchingStrategy;

import java.util.List;
import java.util.UUID;

/**
 * Core service orchestrating the ride lifecycle.
 *
 * SRP : Handles only ride business logic.
 * OCP : New matching/fare strategies plug in without modifying this class.
 * DIP : Depends on abstractions (RideMatchingStrategy, FareStrategy,
 *       RideRepository) injected via constructor — never on concrete types.
 */
public class RideService {

    private final RideRepository       rideRepository;
    private final RiderService         riderService;
    private final DriverService        driverService;
    private final RideMatchingStrategy matchingStrategy;
    private final FareStrategy         fareStrategy;

    /**
     * Constructor injection of all dependencies.
     * This is the DIP in action: RideService never knows which concrete
     * strategy or repository implementation it works with.
     */
    public RideService(RideRepository rideRepository,
                       RiderService riderService,
                       DriverService driverService,
                       RideMatchingStrategy matchingStrategy,
                       FareStrategy fareStrategy) {
        this.rideRepository   = rideRepository;
        this.riderService     = riderService;
        this.driverService    = driverService;
        this.matchingStrategy = matchingStrategy;
        this.fareStrategy     = fareStrategy;
    }

    /**
     * Requests a new ride for the given rider.
     * Ride flow:
     * 1. Create ride with REQUESTED status.
     * 2. Ask matching strategy to select a driver.
     * 3. Assign driver and change status to ASSIGNED.
     * 4. Mark driver as unavailable.
     *
     * @param riderId  the rider's unique ID
     * @param distance trip distance in kilometres
     * @return the newly created and assigned Ride
     * @throws NoDriverAvailableException if no driver is available
     */
    public Ride requestRide(String riderId, double distance) {
        // Validate rider exists (throws RiderNotFoundException if not)
        var rider = riderService.getRiderById(riderId);

        // Fetch available drivers from the driver service
        List<Driver> availableDrivers = driverService.getAvailableDrivers();

        if (availableDrivers.isEmpty()) {
            throw new NoDriverAvailableException();
        }

        // Step 1: Create ride with REQUESTED status
        String rideId = "RIDE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Ride ride = new Ride(rideId, rider, distance);
        rideRepository.save(ride);
        System.out.println("  → Ride created with status REQUESTED (ID: " + rideId + ")");

        // Step 2: Find best driver using the injected matching strategy
        Driver selectedDriver = matchingStrategy.findDriver(rider, availableDrivers);

        if (selectedDriver == null) {
            throw new NoDriverAvailableException();
        }

        // Step 3: Assign driver and update status
        ride.setDriver(selectedDriver);
        ride.setStatus(RideStatus.ASSIGNED);
        rideRepository.save(ride);

        // Step 4: Mark driver as unavailable
        driverService.updateAvailability(selectedDriver.getId(), false);

        System.out.println("  → Driver assigned: " + selectedDriver.getName()
                           + " | Status: ASSIGNED");
        return ride;
    }

    /**
     * Completes a ride.
     * 1. Validates the ride is in ASSIGNED state.
     * 2. Sets status to COMPLETED.
     * 3. Marks driver available again.
     * 4. Increments driver's completed ride count.
     *
     * @param rideId the ride's unique ID
     * @return the updated Ride
     */
    public Ride completeRide(String rideId) {
        Ride ride = getRideById(rideId);

        if (ride.getStatus() != RideStatus.ASSIGNED) {
            throw new IllegalStateException(
                "Cannot complete a ride that is not in ASSIGNED state. Current status: "
                + ride.getStatus());
        }

        ride.setStatus(RideStatus.COMPLETED);
        rideRepository.save(ride);

        // Re-enable the driver
        Driver driver = ride.getDriver();
        driverService.updateAvailability(driver.getId(), true);
        driver.incrementRidesCompleted();

        System.out.println("  → Ride " + rideId + " COMPLETED. Driver "
                           + driver.getName() + " is now available.");
        return ride;
    }

    /**
     * Cancels a ride.
     * 1. Validates the ride is in REQUESTED or ASSIGNED state.
     * 2. Sets status to CANCELLED.
     * 3. Re-enables the driver if one was assigned.
     *
     * @param rideId the ride's unique ID
     * @return the updated Ride
     */
    public Ride cancelRide(String rideId) {
        Ride ride = getRideById(rideId);

        if (ride.getStatus() == RideStatus.COMPLETED
                || ride.getStatus() == RideStatus.CANCELLED) {
            throw new IllegalStateException(
                "Cannot cancel a ride that is already " + ride.getStatus());
        }

        ride.setStatus(RideStatus.CANCELLED);
        rideRepository.save(ride);

        // If a driver was already assigned, free them up
        if (ride.getDriver() != null) {
            driverService.updateAvailability(ride.getDriver().getId(), true);
            System.out.println("  → Driver " + ride.getDriver().getName()
                               + " is now available again.");
        }

        System.out.println("  → Ride " + rideId + " has been CANCELLED.");
        return ride;
    }

    /**
     * Generates a fare receipt for a completed ride.
     * Uses the injected FareStrategy — RideService never knows the pricing formula.
     *
     * @param rideId the completed ride's unique ID
     * @return a FareReceipt with the calculated amount
     */
    public FareReceipt generateFareReceipt(String rideId) {
        Ride ride = getRideById(rideId);

        if (ride.getStatus() != RideStatus.COMPLETED) {
            throw new IllegalStateException(
                "Fare receipt can only be generated for COMPLETED rides. Current status: "
                + ride.getStatus());
        }

        // Delegate fare calculation to the strategy (DIP / OCP)
        double fare = fareStrategy.calculateFare(ride);
        return new FareReceipt(rideId, fare);
    }

    /**
     * Returns all rides stored in the system.
     *
     * @return list of all rides
     */
    public List<Ride> getAllRides() {
        return rideRepository.findAll();
    }

    /**
     * Retrieves a ride by ID.
     *
     * @param rideId the ride's unique ID
     * @return the Ride
     * @throws RideNotFoundException if no such ride exists
     */
    public Ride getRideById(String rideId) {
        return rideRepository.findById(rideId)
                .orElseThrow(() -> new RideNotFoundException(rideId));
    }
}
