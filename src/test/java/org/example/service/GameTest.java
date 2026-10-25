package org.example.service;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import org.example.model.Board;
import org.example.model.Orientation;
import org.example.model.Position;
import org.example.model.Vehicle;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GameTest {
    private Board createBoard() {
        Board board = new Board(6, 6);
        board.addVehicle(new Vehicle(
                "A", 2, Orientation.horizontal, new Position(2, 1), true));
        board.addVehicle(new Vehicle(
                "B", 3, Orientation.vertical, new Position(0, 4), false));
        return board;
    }

    private String runGame(Board board, String commands) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream output = new PrintStream(buffer, true, StandardCharsets.UTF_8);
        // Capture console output only in tests, then always restore it.
        PrintStream originalOutput = System.out;
        try {
            System.setOut(output);
            new Game(board, new Scanner(commands)).start();
        } finally {
            System.setOut(originalOutput);
            output.close();
        }
        return buffer.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
    }

    @Test
    void shouldMoveVehicleAndPrintUpdatedBoard() {
        Board board = createBoard();
        String output = runGame(board, "move A 1\nquit\n");

        assertEquals(new Position(2, 2), board.getVehicle("A").getPosition());
        assertTrue(output.contains("Moved A."));
        assertTrue(output.contains(". . A A B ."));
        assertTrue(output.contains("Goodbye!"));
    }

    @Test
    void shouldRejectBlockedOutsideAndZeroMovesWithoutChangingPosition() {
        Board board = createBoard();
        String output = runGame(board, "move A 2\nmove A -2\nmove A 0\nquit\n");

        assertEquals(new Position(2, 1), board.getVehicle("A").getPosition());
        assertEquals(3, output.lines().filter(line -> line.contains("Invalid move")).count());
        assertFalse(output.contains("Moved A."));
    }

    @Test
    void shouldReportInputErrorsAndContinueReadingCommands() {
        Board board = createBoard();
        String output = runGame(board,
                "wrong\nmove\nmove A 1 extra\nmove A text\nmove A 999999999999\n"
                + "move Z 1\nmove A 1\nquit\n");

        assertTrue(output.contains("Unknown command"));
        assertTrue(output.contains("Usage: move <id> <steps>"));
        assertTrue(output.contains("Steps must be a whole number"));
        assertTrue(output.contains("Unknown vehicle: Z"));
        assertEquals(new Position(2, 2), board.getVehicle("A").getPosition());
    }

    @Test
    void shouldShowHelpAndBoardWithoutChangingState() {
        Board board = createBoard();
        String output = runGame(board, "help\nshow\nshow extra\nquit\n");

        String expectedBoard = ". . . . B .\n. . . . B .\n. A A . B .\n"
                + ". . . . . .\n. . . . . .\n. . . . . .\n";
        int initialBoard = output.indexOf(expectedBoard);
        assertTrue(initialBoard >= 0);
        assertTrue(output.indexOf(expectedBoard, initialBoard + 1) > initialBoard);
        assertTrue(output.contains("Usage: show"));
        assertTrue(output.contains("Commands: show | move <id> <steps> | help | quit"));
        assertEquals(new Position(2, 1), board.getVehicle("A").getPosition());
    }

    @Test
    void shouldHandleWhitespaceNegativeStepsAndEndOfInput() {
        Board board = createBoard();
        String output = runGame(board, "\n   MOVE   A   -1   \n");

        assertEquals(new Position(2, 0), board.getVehicle("A").getPosition());
        assertTrue(output.contains("Goodbye!"));
    }

    @Test
    void shouldStopAtQuitWithoutProcessingRemainingInput() {
        Board board = createBoard();
        runGame(board, "QUIT\nmove A 1\n");

        assertEquals(new Position(2, 1), board.getVehicle("A").getPosition());
    }

    @Test
    void shouldRejectExtremeStepsWithoutCorruptingBoard() {
        Board board = createBoard();
        String output = runGame(board,
                "move A -2147483648\nmove A 2147483647\nquit\n");

        assertEquals(new Position(2, 1), board.getVehicle("A").getPosition());
        assertEquals(2, output.lines().filter(line -> line.contains("Invalid move")).count());
    }
}
