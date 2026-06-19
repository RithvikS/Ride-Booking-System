package repository;

import model.Ride;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory repository for Ride entities.
 *
 * SRP : Only manages CRUD operations for rides — no business logic.
 */
public class RideRepository {

    private final Map<String, Ride> store = new HashMap<>();

    /** Persists a ride (insert or update). */
    public void save(Ride ride) {
        store.put(ride.getId(), ride);
    }

    /** Returns a ride by ID wrapped in Optional. */
    public Optional<Ride> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    /** Returns all stored rides. */
    public List<Ride> findAll() {
        return new ArrayList<>(store.values());
    }

    /** Checks whether a ride with the given ID exists. */
    public boolean existsById(String id) {
        return store.containsKey(id);
    }
}
