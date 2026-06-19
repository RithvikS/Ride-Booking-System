package repository;

import model.Rider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory repository for Rider entities.
 *
 * SRP : Only manages CRUD operations for riders — no business logic.
 * DIP : Services depend on this repository, not on any external storage.
 */
public class RiderRepository {

    // Primary data store: riderId → Rider
    private final Map<String, Rider> store = new HashMap<>();

    /** Persists a new rider. */
    public void save(Rider rider) {
        store.put(rider.getId(), rider);
    }

    /** Returns a rider by ID wrapped in Optional to avoid null checks in callers. */
    public Optional<Rider> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    /** Returns an unmodifiable snapshot of all stored riders. */
    public List<Rider> findAll() {
        return new ArrayList<>(store.values());
    }

    /** Checks whether a rider with the given ID exists. */
    public boolean existsById(String id) {
        return store.containsKey(id);
    }
}
