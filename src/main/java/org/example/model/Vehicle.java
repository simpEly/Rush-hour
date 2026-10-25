package org.example.model;

public class Vehicle {
    private final String id;
    private final int length;
    private final Orientation orientation;
    private Position position;
    private final boolean Hero;

    public Vehicle(String id, int length, Orientation orientation, Position position, boolean hero) {
        this.id = id;
        this.length = length;
        this.orientation = orientation;
        this.position = position;
        this.Hero = hero;
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
        return Hero;
    }
    public void setPosition(Position position) {
        this.position = position;
    }

}
