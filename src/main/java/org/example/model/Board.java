package org.example.model;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private final int column;
    private final int row;
    private final List<Vehicle> vehicles;
    private final List<Exit> exits = new ArrayList<>();
    private Vehicle winner;
    public Board(int row, int column) {
        if (row <= 0 || column <= 0) {
            throw new IllegalArgumentException("Board dimensions must be positive");
        }
        this.column = column;
        this.row = row;
        this.vehicles = new ArrayList<>();
    }
    public int getColumn() {
        return column;
    }
    public int getRow() {
        return row;
    }
    public boolean isInside(Position position) {
        int  column = position.getColumn();
        int row = position.getRow();
        return column < this.column && row < this.row && column >= 0 && row >= 0;
    }
    public List<Position> getOccupiedPositions(Vehicle vehicle) {
        List<Position> positions = new ArrayList<>();
        int startrow = vehicle.getPosition().getRow();
        int startrocol = vehicle.getPosition().getColumn();
        for (int i = 0; i < vehicle.getLength(); i++) {

            if (vehicle.getOrientation() == Orientation.vertical) {
                positions.add(new Position(startrow + i, startrocol));

            }
            else {
                positions.add(new Position(startrow, startrocol + i));

            }
        }
        return positions;
    }
    public boolean isVehicleInside(Vehicle vehicle) {
        List<Position> positions = getOccupiedPositions(vehicle);
        for (Position position : positions) {
            if (!isInside(position)) {
                return false;
            }
        }
        return true;
    }
    public boolean isOccupied(Position position) {
        for (Vehicle car : vehicles) {
            for (Position carPosition : getOccupiedPositions(car)) {
                if (carPosition.equals(position)) {
                    return true;
                }
            }
        }
        return false;
    }
    public Vehicle getVehicleAt(Position position) {
        for (Vehicle vehicle : vehicles) {
            if (getOccupiedPositions(vehicle).contains(position)) {
                return vehicle;
            }
        }
        return null;
    }

    public boolean addVehicle(Vehicle vehicle) {
        if (winner != null || vehicle == null || getVehicle(vehicle.getId()) != null) {
            return false;
        }
        if (!isVehicleInside(vehicle)) {
            return false;
        }
        for (Position position : getOccupiedPositions(vehicle)) {
            if (isOccupied(position)) {
                return false;
            }
        }
        vehicles.add(vehicle);
        return true;
    }
    public Vehicle getVehicle(String id) {
        for (Vehicle car : vehicles) {
            if (car.getId().equals(id)) {
                return car;
            }
        }
        return null;
    }
    public List<Vehicle> getVehicles() {
        return List.copyOf(vehicles);
    }

    public boolean moveVehicle(Vehicle vehicle, int steps) {
        if (!canMoveVehicle(vehicle, steps)) {
            return false;
        }
        boolean vertical = vehicle.getOrientation() == Orientation.vertical;
        int start = vertical ? vehicle.getPosition().getRow() : vehicle.getPosition().getColumn();
        long destination = (long) start + steps;
        int limit = vertical ? row : column;
        if (destination < 0 || destination + vehicle.getLength() > limit) {
            vehicles.remove(vehicle);
            winner = vehicle;
        } else {
            Position current = vehicle.getPosition();
            Position next = vertical
                    ? new Position((int) destination, current.getColumn())
                    : new Position(current.getRow(), (int) destination);
            vehicle.setPosition(next);
        }
        return true;
    }

    public boolean canMoveVehicle(Vehicle vehicle, int steps) {
        if (winner != null || vehicle == null || getVehicle(vehicle.getId()) != vehicle) {
            return false;
        }
        if (steps == 0) {
            return false;
        }

        Position current = vehicle.getPosition();
        boolean vertical = vehicle.getOrientation() == Orientation.vertical;
        int start = vertical ? current.getRow() : current.getColumn();
        int lane = vertical ? current.getColumn() : current.getRow();
        int limit = vertical ? row : column;
        int length = vehicle.getLength();
        int sign = Integer.signum(steps);
        long destination = (long) start + steps;
        long end = destination + length;
        boolean leavesBoard = destination < 0 || end > limit;

        if (leavesBoard) {
            Exit exit = findMatchingExit(vehicle);
            boolean completelyOutside = sign > 0 ? destination >= limit : end <= 0;
            if (exit == null || exit.getDirection().getStepSign() != sign || !completelyOutside) {
                return false;
            }
        }

        // Check only newly crossed cells, stopping at the edge for an exit move.
        int first = sign > 0 ? start + length : start - 1;
        int last = sign > 0 ? (int) Math.min(end - 1, limit - 1) : (int) Math.max(destination, 0);
        for (int cell = first; sign > 0 ? cell <= last : cell >= last; cell += sign) {
            Position position = vertical ? new Position(cell, lane) : new Position(lane, cell);
            if (isOccupied(position)) {
                return false;
            }
        }
        return true;
    }

    public void addExit(Exit exit) {
        if (exit == null || winner != null) {
            throw new IllegalArgumentException("Invalid exit configuration");
        }
        boolean vertical = exit.getDirection().getOrientation() == Orientation.vertical;
        int laneCount = vertical ? column : row;
        if (exit.getCoordinate() >= laneCount) {
            throw new IllegalArgumentException("Exit coordinate is outside the board");
        }
        Vehicle hero = getVehicle(exit.getVehicleId());
        if (hero == null || !hero.isHero() || !matchesExit(hero, exit)) {
            throw new IllegalArgumentException("Exit must match its hero's orientation and lane");
        }
        for (Exit existing : exits) {
            if (existing.getVehicleId().equals(exit.getVehicleId())) {
                throw new IllegalArgumentException("Hero already has an exit");
            }
        }
        exits.add(exit);
    }

    public List<Exit> getExits() {
        return List.copyOf(exits);
    }

    public Vehicle getWinner() {
        return winner;
    }

    private Exit findMatchingExit(Vehicle vehicle) {
        if (vehicle == null || !vehicle.isHero()) {
            return null;
        }
        for (Exit exit : exits) {
            if (exit.getVehicleId().equals(vehicle.getId()) && matchesExit(vehicle, exit)) {
                return exit;
            }
        }
        return null;
    }

    private boolean matchesExit(Vehicle vehicle, Exit exit) {
        if (vehicle.getOrientation() != exit.getDirection().getOrientation()) {
            return false;
        }
        Position position = vehicle.getPosition();
        int lane = vehicle.getOrientation() == Orientation.vertical
                ? position.getColumn() : position.getRow();
        return lane == exit.getCoordinate();
    }

    public boolean isIncompleteExit(Vehicle vehicle, int steps) {
        Exit exit = findMatchingExit(vehicle);
        if (exit == null || exit.getDirection().getStepSign() != Integer.signum(steps)) {
            return false;
        }

        boolean vertical = vehicle.getOrientation() == Orientation.vertical;
        int start = vertical ? vehicle.getPosition().getRow() : vehicle.getPosition().getColumn();
        int limit = vertical ? row : column;
        long destination = (long) start + steps;
        long end = destination + vehicle.getLength();
        return steps > 0 ? destination < limit && end > limit : destination < 0 && end > 0;
    }
}
