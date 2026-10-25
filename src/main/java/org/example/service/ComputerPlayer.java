package org.example.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.example.model.Board;
import org.example.model.Move;
import org.example.model.Orientation;
import org.example.model.Vehicle;

public class ComputerPlayer {
    private final Random random;

    public ComputerPlayer() {
        this(new Random());
    }

    public ComputerPlayer(Random random) {
        this.random = random;
    }

    public Move chooseMove(Board board) {
        List<Vehicle> movableVehicles = new ArrayList<>();
        for (Vehicle vehicle : board.getVehicles()) {
            if (!findLegalSteps(board, vehicle).isEmpty()) {
                movableVehicles.add(vehicle);
            }
        }
        if (movableVehicles.isEmpty()) {
            return null;
        }

        Vehicle vehicle = movableVehicles.get(random.nextInt(movableVehicles.size()));
        List<Integer> steps = findLegalSteps(board, vehicle);
        int chosenSteps = steps.get(random.nextInt(steps.size()));
        return new Move(vehicle, chosenSteps);
    }

    public boolean hasLegalMove(Board board) {
        for (Vehicle vehicle : board.getVehicles()) {
            if (!findLegalSteps(board, vehicle).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private List<Integer> findLegalSteps(Board board, Vehicle vehicle) {
        List<Integer> steps = new ArrayList<>();
        boolean vertical = vehicle.getOrientation() == Orientation.vertical;
        int start = vertical ? vehicle.getPosition().getRow() : vehicle.getPosition().getColumn();
        int limit = vertical ? board.getRow() : board.getColumn();
        // Include exactly enough movement to exit completely on either side.
        for (int step = -start - vehicle.getLength(); step <= limit - start; step++) {
            if (board.canMoveVehicle(vehicle, step)) {
                steps.add(step);
            }
        }
        return steps;
    }
}
