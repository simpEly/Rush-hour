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
    public boolean moveVehicle(Vehicle vehicle, int steps) {
        if (winner != null || vehicle == null || getVehicle(vehicle.getId()) != vehicle) {
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
            return tryExit(vehicle, steps, destination);
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

    public void addExit(Exit exit) {
        if (exit == null || exit.getRow() >= row || winner != null) {
            throw new IllegalArgumentException("Invalid exit configuration");
        }
        Vehicle hero = getVehicle(exit.getVehicleId());
        if (hero == null || !hero.isHero() || hero.getOrientation() != Orientation.horizontal
                || hero.getPosition().getRow() != exit.getRow()) {
            throw new IllegalArgumentException("Exit must match a horizontal hero on its row");
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

    private boolean tryExit(Vehicle vehicle, int steps, long destination) {
        if (!vehicle.isHero() || vehicle.getOrientation() != Orientation.horizontal) {
            return false;
        }
        for (Exit exit : exits) {
            if (!exit.getVehicleId().equals(vehicle.getId())
                    || exit.getRow() != vehicle.getPosition().getRow()) {
                continue;
            }
            boolean right = exit.isRightSide();
            // Exit is one complete move: the entire vehicle must leave the board.
            if (right && (steps <= 0 || destination < column)) {
                return false;
            }
            if (!right && (steps >= 0 || destination + vehicle.getLength() > 0)) {
                return false;
            }

            int direction = right ? 1 : -1;
            int nextColumn = right
                    ? vehicle.getPosition().getColumn() + vehicle.getLength()
                    : vehicle.getPosition().getColumn() - 1;
            // Check every cell between the front of the hero and its exit.
            while (nextColumn >= 0 && nextColumn < column) {
                if (isOccupied(new Position(exit.getRow(), nextColumn))) {
                    return false;
                }
                nextColumn += direction;
            }
            vehicles.remove(vehicle);
            winner = vehicle;
            return true;
        }
        return false;
    }

}
