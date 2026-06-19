package service;

import exception.RiderNotFoundException;
import model.Rider;
import repository.RiderRepository;

import java.util.List;

/**
 * Service layer for all rider-related operations.
 *
 * SRP : Handles only rider business logic.
 * DIP : Depends on RiderRepository abstraction injected via constructor.
 */
public class RiderService {

    private final RiderRepository riderRepository;

    // Constructor injection — satisfies DIP
    public RiderService(RiderRepository riderRepository) {
        this.riderRepository = riderRepository;
    }

    /**
     * Registers a new rider in the system.
     *
     * @param id       unique rider identifier
     * @param name     rider's full name
     * @param location rider's current location
     * @return the newly created Rider
     */
    public Rider registerRider(String id, String name, String location) {
        Rider rider = new Rider(id, name, location);
        riderRepository.save(rider);
        System.out.println("✔ Rider registered successfully: " + rider);
        return rider;
    }

    /**
     * Retrieves a rider by ID.
     *
     * @param riderId the rider's unique ID
     * @return the Rider if found
     * @throws RiderNotFoundException if no rider exists with that ID
     */
    public Rider getRiderById(String riderId) {
        return riderRepository.findById(riderId)
                .orElseThrow(() -> new RiderNotFoundException(riderId));
    }

    /**
     * Lists all registered riders.
     *
     * @return list of all riders
     */
    public List<Rider> getAllRiders() {
        return riderRepository.findAll();
    }
}
