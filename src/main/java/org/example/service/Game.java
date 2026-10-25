package org.example.service;

import java.util.Scanner;
import org.example.model.Board;
import org.example.model.Vehicle;
import org.example.ui.BoardPrinter;

public class Game {
    private final Board board;
    private final Scanner input;
    private final BoardPrinter printer;

    public Game(Board board, Scanner input) {
        this.board = board;
        this.input = input;
        this.printer = new BoardPrinter();
    }

    public void start() {
        System.out.println("Rush Hour - hero escape demo");
        showHelp();
        printer.print(board);

        while (board.getWinner() == null) {
            System.out.print("> ");
            // End of input (for example Ctrl+D) also ends the program safely.
            if (!input.hasNextLine()) {
                break;
            }
            String line = input.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }
            String[] parts = line.split("\\s+");
            String command = parts[0];
            if (command.equalsIgnoreCase("quit") && parts.length == 1) {
                break;
            }
            if (command.equalsIgnoreCase("show")) {
                if (parts.length == 1) {
                    printer.print(board);
                } else {
                    System.out.println("Usage: show");
                }
            } else if (command.equalsIgnoreCase("help")) {
                showHelp();
            } else if (command.equalsIgnoreCase("move")) {
                handleMove(parts);
            } else {
                System.out.println("Unknown command. Type help.");
            }
        }
        if (board.getWinner() != null) {
            System.out.println("Hero " + board.getWinner().getId() + " wins!");
        }
        System.out.println("Goodbye!");
    }

    private void showHelp() {
        System.out.println("Commands: show | move <id> <steps> | help | quit");
        System.out.println("Positive steps: right/down. Negative steps: left/up.");
        System.out.println("Example: move A 2. Vehicle IDs are case-sensitive.");
        System.out.println("To win, move the entire hero out through its own exit in one move.");
    }

    private void handleMove(String[] parts) {
        if (parts.length != 3) {
            System.out.println("Usage: move <id> <steps>");
            return;
        }

        int steps;
        try {
            steps = Integer.parseInt(parts[2]);
        } catch (NumberFormatException exception) {
            System.out.println("Steps must be a whole number within the integer range.");
            return;
        }

        Vehicle vehicle = board.getVehicle(parts[1]);
        if (vehicle == null) {
            System.out.println("Unknown vehicle: " + parts[1]);
            return;
        }
        if (!board.moveVehicle(vehicle, steps)) {
            System.out.println("Invalid move: blocked, outside board, or zero steps.");
            return;
        }

        System.out.println("Moved " + vehicle.getId() + ".");
        printer.print(board);
    }
}
