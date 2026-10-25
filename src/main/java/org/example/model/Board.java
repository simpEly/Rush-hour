package org.example.model;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private final int column;
    private final int row;
    private final List<Vehicle> vehicles;
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
        if (vehicle == null || getVehicle(vehicle.getId()) != null) {
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
    public boolean moveVehicle(Vehicle vehicle, int steps) {
        if (vehicle == null || getVehicle(vehicle.getId()) != vehicle) {
            return false;
        }
        if (steps == 0) return false;
        int row =  vehicle.getPosition().getRow();
        int column = vehicle.getPosition().getColumn();
        int length = vehicle.getLength();
        // Check the destination first, including very large input values.
        boolean vertical = vehicle.getOrientation() == Orientation.vertical;
        long destination = (long) (vertical ? row : column) + steps;
        int limit = vertical ? this.row : this.column;
        if (destination < 0 || destination + length > limit) {
            return false;
        }
        if (vehicle.getOrientation() == Orientation.vertical) {
            for (int i = 0; i < Math.abs(steps); i++) {
                if (steps > 0) {
                    Position position = new Position(row + length + i, column);
                    if (isOccupied(position) || !isInside(position)) {
                        return false;
                    }
                }
                else {
                    Position position = new Position(row - i - 1, column);
                    if (isOccupied(position) || !isInside(position))    {
                        return false;
                    }
                }
            }
            Position position = new Position(row + steps, column);
            vehicle.setPosition(position);

        }
        else {
            for (int i = 0; i < Math.abs(steps); i++) {
                if (steps > 0) {
                    Position position = new Position(row, column + length + i);
                    if (isOccupied(position) || !isInside(position)) {
                        return false;
                    }
                }
                else {
                    Position position = new Position(row, column - i - 1);
                    if (isOccupied(position) || !isInside(position)) {
                        return false;
                    }
                }
            }
            Position position = new Position(row, column + steps);
            vehicle.setPosition(position);
        }

        return true;
    }

}
