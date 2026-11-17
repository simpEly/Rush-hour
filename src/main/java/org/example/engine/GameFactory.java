package org.example.engine;


import org.example.model.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class GameFactory {
    public static Board createDefaultBoard() {
        Board board = new Board(6, 6);
        board.addVehicle(new Vehicle(
                "A", 2, Orientation.horizontal, new Position(2, 1), true
        ));
        board.addVehicle(new Vehicle(
                "B", 2, Orientation.horizontal, new Position(4, 2), true
        ));
        board.addVehicle(new Vehicle(
                "C", 3, Orientation.vertical, new Position(0, 4), false
        ));
        board.addVehicle(new Vehicle(
                "D", 3, Orientation.vertical, new Position(3, 1), false
        ));
        board.addExit(new Exit("A", 2, true));
        board.addExit(new Exit("B", 4, false));
        return board;
    }

    public static Board loadBoardFromFile(String path) throws IOException {
        Board board = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty()) {
                    continue;
                }

                String[] parts = line.split("\\s+");

                switch (parts[0]) {
                    case "BOARD":
                        if (parts.length != 3 || board != null) {
                            throw new IllegalArgumentException("Invalid BOARD line");
                        }

                        int rows = Integer.parseInt(parts[1]);
                        int cols = Integer.parseInt(parts[2]);

                        if (rows <= 0 || cols <= 0) {
                            throw new IllegalArgumentException("Invalid board size");
                        }

                        board = new Board(rows, cols);
                        break;

                    case "VEHICLE":
                        if (board == null || parts.length != 7) {
                            throw new IllegalArgumentException("Invalid VEHICLE line");
                        }

                        String id = parts[1];
                        int length = Integer.parseInt(parts[2]);

                        Orientation orientation;

                        if (parts[3].equals("H")) {
                            orientation = Orientation.horizontal;
                        } else if (parts[3].equals("V")) {
                            orientation = Orientation.vertical;
                        } else {
                            throw new IllegalArgumentException("Invalid orientation");
                        }

                        int vehicleRow = Integer.parseInt(parts[4]);
                        int vehicleCol = Integer.parseInt(parts[5]);

                        if (!parts[6].equals("true") &&
                                !parts[6].equals("false")) {
                            throw new IllegalArgumentException("Invalid hero value");
                        }

                        boolean hero = Boolean.parseBoolean(parts[6]);

                        Vehicle vehicle = new Vehicle(
                                id,
                                length,
                                orientation,
                                new Position(vehicleRow, vehicleCol),
                                hero
                        );

                        if (!board.addVehicle(vehicle)) {
                            throw new IllegalArgumentException("Invalid vehicle: " + id);
                        }
                        break;

                    case "EXIT":
                        if (board == null || parts.length != 4) {
                            throw new IllegalArgumentException("Invalid EXIT line");
                        }

                        String vehicleId = parts[1];
                        int exitRow = Integer.parseInt(parts[2]);

                        boolean rightSide;

                        if (parts[3].equals("RIGHT")) {
                            rightSide = true;
                        } else if (parts[3].equals("LEFT")) {
                            rightSide = false;
                        } else {
                            throw new IllegalArgumentException("Invalid exit side");
                        }

                        board.addExit(new Exit(vehicleId, exitRow, rightSide));
                        break;

                    default:
                        throw new IllegalArgumentException(
                                "Unknown line type: " + parts[0]
                        );
                }
            }
        }

        if (board == null) {
            throw new IllegalArgumentException("Missing BOARD");
        }

        long heroCount = board.getVehicles().stream()
                .filter(Vehicle::isHero)
                .count();

        if (heroCount != 2) {
            throw new IllegalArgumentException("Exactly two heroes required");
        }

        for (Vehicle v : board.getVehicles()) {
            if (v.isHero()) {
                boolean hasExit = false;

                for (Exit exit : board.getExits()) {
                    if (exit.getVehicleId().equals(v.getId())) {
                        hasExit = true;
                        break;
                    }
                }

                if (!hasExit) {
                    throw new IllegalArgumentException(
                            "Missing exit for hero: " + v.getId()
                    );
                }
            }
        }

        return board;
    }

    public static Board createInitialBoard(String path) {
        try {
            return loadBoardFromFile(path);
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Cannot load level: " + e.getMessage());
            System.out.println("Default level load");
            return createDefaultBoard();
        }
    }
    public static void saveBoard(Board board, String path)
            throws IOException {

        String content = "BOARD " + board.getRow()
                + " " + board.getColumn() + "\n";

        for (Vehicle vehicle : board.getVehicles()) {
            Position position = vehicle.getPosition();

            String orientation =
                    vehicle.getOrientation() == Orientation.horizontal
                            ? "H" : "V";

            content += "VEHICLE "
                    + vehicle.getId() + " "
                    + vehicle.getLength() + " "
                    + orientation + " "
                    + position.getRow() + " "
                    + position.getColumn() + " "
                    + vehicle.isHero() + "\n";
        }

        for (Exit exit : board.getExits()) {
            content += "EXIT "
                    + exit.getVehicleId() + " "
                    + exit.getRow() + " "
                    + (exit.isRightSide() ? "RIGHT" : "LEFT") + "\n";
        }



        Files.writeString(Path.of(path), content);
    }



}
