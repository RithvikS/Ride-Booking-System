package service;

import exception.DriverNotFoundException;
import model.Driver;
import repository.DriverRepository;

import java.util.List;

/**
 * Service layer for all driver-related operations.
 *
 * SRP : Handles only driver business logic.
 * DIP : Depends on DriverRepository injected via constructor.
 */
public class DriverService {

    private final DriverRepository driverRepository;

    // Constructor injection — satisfies DIP
    public DriverService(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    /**
     * Registers a new driver in the system.
     */
    public Driver registerDriver(String id, String name, String location) {
        Driver driver = new Driver(id, name, location);
        driverRepository.save(driver);
        System.out.println("✔ Driver registered successfully: " + driver);
        return driver;
    }

    /**
     * Retrieves a driver by ID.
     *
     * @param driverId the driver's unique ID
     * @return the Driver if found
     * @throws DriverNotFoundException if no driver exists with that ID
     */
    public Driver getDriverById(String driverId) {
        return driverRepository.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException(driverId));
    }

    public void updateAvailability(String driverId, boolean available) {
        Driver driver = getDriverById(driverId);
        driver.setAvailable(available);
        driverRepository.save(driver);
    }

    /**
     * Returns all currently available drivers.
     */
    public List<Driver> getAvailableDrivers() {
        return driverRepository.findAvailable();
    }

    /**
     * Returns all registered drivers.
     */
    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }
}
