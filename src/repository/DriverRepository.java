package repository;

import model.Driver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * In-memory repository for Driver entities.
 *
 * SRP : Only manages CRUD operations for drivers — no business logic.
 */
public class DriverRepository {

    private final Map<String, Driver> store = new HashMap<>();

    /** Persists a new driver. */
    public void save(Driver driver) {
        store.put(driver.getId(), driver);
    }

    /** Returns a driver by ID wrapped in Optional. */
    public Optional<Driver> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    /** Returns all stored drivers. */
    public List<Driver> findAll() {
        return new ArrayList<>(store.values());
    }

    /** Returns only drivers whose available flag is true. */
    public List<Driver> findAvailable() {
        return store.values().stream()
                .filter(Driver::isAvailable)
                .collect(Collectors.toList());
    }

    /** Checks whether a driver with the given ID exists. */
    public boolean existsById(String id) {
        return store.containsKey(id);
    }
}
