package org.example.service;

import java.io.IOException;
import java.util.Scanner;

import org.example.engine.GameFactory;
import org.example.model.Board;
import org.example.model.Move;
import org.example.model.Vehicle;
import org.example.ui.BoardPrinter;
import org.example.database.PlayerRepository;
import org.example.model.Turn;
import org.example.model.SavedGame;

public class Game {
    private final String humanHeroId = "A";
    private final String computerHeroId = "B";
    private final Board board;
    private Turn turn = Turn.HUMAN;
    private final Scanner input;
    private final BoardPrinter printer;
    private final ComputerPlayer computerPlayer;
    private final String playerName;
    private final PlayerRepository repository;



    public Game(SavedGame savedGame, Scanner input,
                String playerName, PlayerRepository repository) {
        this(savedGame, input, new ComputerPlayer(), playerName, repository);
    }



    public Game(SavedGame savedGame, Scanner input,
                ComputerPlayer computerPlayer,
                String playerName, PlayerRepository repository) {
        this.board = savedGame.board();
        this.turn = savedGame.turn();
        this.input = input;
        this.printer = new BoardPrinter();
        this.computerPlayer = computerPlayer;
        this.playerName = playerName;
        this.repository = repository;
    }



    public void start() {
        System.out.println("Rush Hour - human vs computer");
        System.out.println("Your hero: " + humanHeroId + " -> right exit");
        System.out.println("Computer hero: " + computerHeroId + " -> left exit");
        System.out.println("Each player may move any vehicle.");
        showHelp();
        printer.print(board);

        while (board.getWinner() == null) {
            if (!computerPlayer.hasLegalMove(board)) {
                System.out.println("No legal moves remain. Draw.");
                break;
            }

            if (turn == Turn.COMPUTER) {
                playComputerTurn();
                turn = Turn.HUMAN;
                continue;
            }
            System.out.print("Your turn > ");
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
            } else if (command.equalsIgnoreCase("save")) {
                if (parts.length == 2) {
                    handleSave(parts[1]);
                } else {
                    System.out.println("Usage: save <username>");
                }
            } else if (command.equalsIgnoreCase("highscore")) {
                if (parts.length == 1) {
                    repository.showHighScores();
                } else {
                    System.out.println("Usage: highscore");
                }
            } else if (command.equalsIgnoreCase("move")) {
                boolean moved = handleMove(parts);
                if (moved && board.getWinner() == null) {
                    turn = Turn.COMPUTER;
                }
            } else {
                System.out.println("Unknown command. Type help.");
            }
        }
        if (board.getWinner() != null) {
            String winnerId = board.getWinner().getId();

            if (winnerId.equals(humanHeroId)) {
                System.out.println("You win! Hero " + winnerId + " escaped.");

                repository.addWin(playerName);
                repository.showHighScores();

            } else if (winnerId.equals(computerHeroId)) {
                System.out.println("Computer wins! Hero " + winnerId + " escaped.");
            } else {
                System.out.println("Hero " + winnerId + " wins!");
            }
        }
    }

    private void showHelp() {
        System.out.println("Commands: show | move <id> <steps> | help | quit");
        System.out.println("Positive steps: right/down. Negative steps: left/up.");
        System.out.println("Example: move A 2. Vehicle IDs are case-sensitive.");
        System.out.println("To win, move the entire hero out through its own exit in one move.");
        System.out.println("save + username - Save current game.");
        System.out.println("Commands: show | move <id> <steps> | save | highscore | help | quit");
    }

    private void handleSave(String name) {
        if (!name.matches("[a-zA-Z0-9_-]+")) {
            System.out.println("Invalid save name.");
            return;
        }

        String path = "saves/" + name + ".txt";

        try {
            GameFactory.saveBoard(board, turn, path);
            System.out.println("Game saved successfully.");
        } catch (IOException e) {
            System.out.println("Cannot save game: " + e.getMessage());
        }
    }


    private boolean handleMove(String[] parts) {
        if (parts.length != 3) {
            System.out.println("Usage: move <id> <steps>");
            return false;
        }

        int steps;
        try {
            steps = Integer.parseInt(parts[2]);
        } catch (NumberFormatException exception) {
            System.out.println("Steps must be a whole number within the integer range.");
            return false;
        }

        Vehicle vehicle = board.getVehicle(parts[1]);
        if (vehicle == null) {
            System.out.println("Unknown vehicle: " + parts[1]);
            return false;
        }
        if (!board.moveVehicle(vehicle, steps)) {
            if (board.isIncompleteExit(vehicle, steps)) {
                System.out.println("Vehicle must exit the board completely.");
            } else {
                System.out.println("Invalid move: blocked, outside board, or zero steps.");
            }
            return false;
        }

        System.out.println("Moved " + vehicle.getId() + ".");
        printer.print(board);
        return true;
    }

    private void playComputerTurn() {
        Move move = computerPlayer.chooseMove(board);
        if (move == null) {
            System.out.println("Computer has no legal move.");
            return;
        }
        board.moveVehicle(move.getVehicle(), move.getSteps());
        System.out.println("Computer: move " + move.getVehicle().getId() + " " + move.getSteps());
        printer.print(board);
    }

}
