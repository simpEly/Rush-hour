package org.example.service;

import java.util.Scanner;
import org.example.model.Board;
import org.example.model.Exit;
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

    private void runGame(Board board, String commands) {
        Scanner input = new Scanner(commands);
        Game game = new Game(board, input);
        game.start();
    }

    @Test
    void shouldMoveVehicleFromCommand() {
        Board board = createBoard();

        runGame(board, "move A 1\nquit\n");

        assertEquals(new Position(2, 2), board.getVehicle("A").getPosition());
        assertNull(board.getVehicleAt(new Position(2, 1)));
        assertSame(board.getVehicle("A"), board.getVehicleAt(new Position(2, 3)));
    }

    @Test
    void shouldRejectBlockedOutsideAndZeroMoves() {
        String[] commands = {"move A 2", "move A -2", "move A 0"};

        for (String command : commands) {
            Board board = createBoard();

            runGame(board, command + "\nquit\n");

            assertEquals(new Position(2, 1), board.getVehicle("A").getPosition(), command);
            assertEquals(new Position(0, 4), board.getVehicle("B").getPosition(), command);
        }
    }

    @Test
    void shouldIgnoreInvalidInputAndContinueReadingCommands() {
        String[] commands = {
            "wrong", "move", "move A 1 extra", "move A text",
            "move A 999999999999", "move Z 1"
        };

        for (String command : commands) {
            Board board = createBoard();
            runGame(board, command + "\nquit\n");
            assertEquals(new Position(2, 1), board.getVehicle("A").getPosition(), command);

            Board anotherBoard = createBoard();
            runGame(anotherBoard, command + "\nmove A 1\nquit\n");
            assertEquals(new Position(2, 2), anotherBoard.getVehicle("A").getPosition(), command);
        }
    }

    @Test
    void shouldLeaveBoardUnchangedAfterShowAndHelp() {
        Board board = createBoard();

        runGame(board, "help\nshow\nshow extra\nquit\n");

        assertEquals(new Position(2, 1), board.getVehicle("A").getPosition());
        assertEquals(new Position(0, 4), board.getVehicle("B").getPosition());
    }

    @Test
    void shouldHandleWhitespaceNegativeStepsAndEndOfInput() {
        Board board = createBoard();
        Scanner input = new Scanner("\n   MOVE   A   -1   \n");

        new Game(board, input).start();

        assertEquals(new Position(2, 0), board.getVehicle("A").getPosition());
        assertFalse(input.hasNextLine());
    }

    @Test
    void shouldStopAtQuitWithoutProcessingRemainingInput() {
        Board board = createBoard();
        Scanner input = new Scanner("QUIT\nmove A 1\n");

        new Game(board, input).start();

        assertEquals(new Position(2, 1), board.getVehicle("A").getPosition());
        assertEquals("move A 1", input.nextLine());
    }

    @Test
    void shouldRejectExtremeStepsWithoutChangingPosition() {
        String[] commands = {"move A -2147483648", "move A 2147483647"};

        for (String command : commands) {
            Board board = createBoard();

            runGame(board, command + "\nquit\n");

            assertEquals(new Position(2, 1), board.getVehicle("A").getPosition(), command);
        }
    }
    @Test
    void shouldStopReadingCommandsWhenHeroWins() {
        Board board = createBoard();
        board.addExit(new Exit("A", 2, true));
        Scanner input = new Scanner("move B 3\nmove A 5\nmove B -1\n");
        Vehicle hero = board.getVehicle("A");

        new Game(board, input).start();

        assertSame(hero, board.getWinner());
        assertNull(board.getVehicle("A"));
        assertEquals(new Position(3, 4), board.getVehicle("B").getPosition());
        assertEquals("move B -1", input.nextLine());
    }

}
