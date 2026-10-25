package org.example.model;

/** A vehicle and the number of cells it should move. */
public class Move {
    private final Vehicle vehicle;
    private final int steps;

    public Move(Vehicle vehicle, int steps) {
        this.vehicle = vehicle;
        this.steps = steps;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public int getSteps() {
        return steps;
    }
}
