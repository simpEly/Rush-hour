package org.example.model;

public class Vehicle {
    private final String id;
    private final int length;
    private final Orientation orientation;
    private Position position;
    private final boolean hero;

    public Vehicle(String id, int length, Orientation orientation, Position position, boolean hero) {
        if (id == null || !id.matches("\\S+")) {
            throw new IllegalArgumentException("Vehicle ID must not be empty or contain whitespace");
        }
        if (length <= 0) {
            throw new IllegalArgumentException("Vehicle length must be positive");
        }
        if (orientation == null || position == null) {
            throw new IllegalArgumentException("Vehicle orientation and position are required");
        }
        this.id = id;
        this.length = length;
        this.orientation = orientation;
        this.position = position;
        this.hero = hero;
    }
    public String getId() {
        return id;
    }
    public int getLength() {
        return length;
    }
    public Orientation getOrientation() {
        return orientation;
    }
    public Position getPosition() {
        return position;
    }
    public boolean isHero() {
        return hero;
    }
    public void setPosition(Position position) {
        if (position == null) {
            throw new IllegalArgumentException("Vehicle position is required");
        }
        this.position = position;
    }

}
