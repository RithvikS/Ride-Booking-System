package model;

/**
 * Represents a rider who books rides on the platform.
 * SRP: Only holds rider data, no business logic.
 */
public class Rider {

    private final String id;
    private String name;
    private String location;

    public Rider(String id, String name, String location) {
        this.id = id;
        this.name = name;
        this.location = location;
    }

    // ---------- Getters & Setters ----------

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return String.format("Rider[id=%s, name=%s, location=%s]", id, name, location);
    }
}
