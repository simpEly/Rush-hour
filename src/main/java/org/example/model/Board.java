package org.example.model;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private final int column;
    private final int row;
    private final List<Vehicle> vehicles;
    public Board(int row, int column) {
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
    public boolean addVehicle(Vehicle vehicle) {
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
}
