package org.example.service;

import java.io.IOException;
import java.util.Scanner;
import org.example.engine.GameFactory;
import org.example.model.SavedGame;
import org.example.model.Turn;
import org.example.database.PlayerRepository;
import org.example.model.Board;
import org.example.model.Exit;
import org.example.model.ExitDirection;
import org.example.model.Move;
import org.example.model.Orientation;
import org.example.model.Position;
import org.example.model.Vehicle;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameTest {
    private final PlayerRepository repository = mock(PlayerRepository.class);
    private static final String PLAYER = "Test player";

    // Predictable movement keeps command tests independent of random choices.
    private static class PredictableComputer extends ComputerPlayer {
        private int turnCount;

        @Override
        public Move chooseMove(Board board) {
            turnCount++;
            Vehicle blocker = board.getVehicle("B");
            int steps = blocker.getPosition().getRow() == 0 ? 1 : -1;
            return new Move(blocker, steps);
        }
    }

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
        Game game = new Game(new SavedGame(board, Turn.HUMAN), input, new PredictableComputer(), PLAYER, repository);
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

        new Game(new SavedGame(board, Turn.HUMAN), input, new PredictableComputer(), PLAYER, repository).start();

        assertEquals(new Position(2, 0), board.getVehicle("A").getPosition());
        assertFalse(input.hasNextLine());
    }

    @Test
    void shouldStopAtQuitWithoutProcessingRemainingInput() {
        Board board = createBoard();
        Scanner input = new Scanner("QUIT\nmove A 1\n");

        new Game(new SavedGame(board, Turn.HUMAN), input, new PredictableComputer(), PLAYER, repository).start();

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
        board.addExit(new Exit("A", 2, ExitDirection.RIGHT));
        assertTrue(board.moveVehicle(board.getVehicle("B"), 3));
        Scanner input = new Scanner("move A 5\nmove B -1\n");
        Vehicle hero = board.getVehicle("A");

        new Game(new SavedGame(board, Turn.HUMAN), input, new PredictableComputer(), PLAYER, repository).start();

        assertSame(hero, board.getWinner());
        assertNull(board.getVehicle("A"));
        assertEquals(new Position(3, 4), board.getVehicle("B").getPosition());
        assertEquals("move B -1", input.nextLine());
    }

    @Test
    void shouldAllowComputerHeroToWinThroughItsOwnExit() {
        Board board = new Board(6, 6);
        Vehicle humanHero = new Vehicle("A", 2, Orientation.horizontal, new Position(2, 1), true);
        Vehicle computerHero = new Vehicle("B", 2, Orientation.horizontal, new Position(4, 1), true);
        assertTrue(board.addVehicle(humanHero));
        assertTrue(board.addVehicle(computerHero));
        board.addExit(new Exit("A", 2, ExitDirection.RIGHT));
        board.addExit(new Exit("B", 4, ExitDirection.LEFT));
        Scanner input = new Scanner("move B -3\nmove A 1\n");

        new Game(new SavedGame(board, Turn.HUMAN), input, new PredictableComputer(), PLAYER, repository).start();

        assertSame(computerHero, board.getWinner());
        assertSame(humanHero, board.getVehicle("A"));
        assertNull(board.getVehicle("B"));
        assertEquals(new Position(2, 1), humanHero.getPosition());
        assertEquals("move A 1", input.nextLine());
    }

    @Test
    void shouldGiveComputerOneTurnOnlyAfterValidHumanMove() {
        Board board = createBoard();
        PredictableComputer computer = new PredictableComputer();
        Scanner input = new Scanner("help\nshow\nwrong\nmove A text\nmove A 0\n"
                + "move A 2\nmove A 1\nquit\n");

        new Game(new SavedGame(board, Turn.HUMAN), input, computer, PLAYER, repository).start();

        assertEquals(1, computer.turnCount);
        assertEquals(new Position(2, 2), board.getVehicle("A").getPosition());
        assertEquals(new Position(1, 4), board.getVehicle("B").getPosition());
    }

    @Test
    void shouldNotGiveComputerTurnAfterHumanWins() {
        Board board = new Board(1, 6);
        Vehicle hero = new Vehicle("A", 2, Orientation.horizontal, new Position(0, 0), true);
        board.addVehicle(hero);
        board.addExit(new Exit("A", 0, ExitDirection.RIGHT));
        PredictableComputer computer = new PredictableComputer();

        new Game(new SavedGame(board, Turn.HUMAN), new Scanner("move A 6\n"), computer, PLAYER, repository).start();

        assertSame(hero, board.getWinner());
        assertEquals(0, computer.turnCount);
        var scoreUpdates = inOrder(repository);
        scoreUpdates.verify(repository).addWin(PLAYER);
        scoreUpdates.verify(repository).showHighScores();
        scoreUpdates.verifyNoMoreInteractions();
    }

    @Test
    void shouldStopInputImmediatelyWhenComputerWins() {
        Board board = new Board(2, 6);
        Vehicle human = new Vehicle("A", 2, Orientation.horizontal, new Position(0, 0), true);
        Vehicle machine = new Vehicle("B", 2, Orientation.horizontal, new Position(1, 1), true);
        board.addVehicle(human);
        board.addVehicle(machine);
        board.addExit(new Exit("A", 0, ExitDirection.RIGHT));
        board.addExit(new Exit("B", 1, ExitDirection.LEFT));
        ComputerPlayer computer = new ComputerPlayer() {
            @Override
            public Move chooseMove(Board currentBoard) {
                return new Move(machine, -3);
            }
        };
        Scanner input = new Scanner("move A 1\nmove A 1\n");

        new Game(new SavedGame(board, Turn.HUMAN), input, computer, PLAYER, repository).start();

        assertSame(machine, board.getWinner());
        verifyNoInteractions(repository);
        assertEquals(new Position(0, 1), human.getPosition());
        assertEquals("move A 1", input.nextLine());
    }

    @Test
    void shouldFinishWithoutReadingInputWhenNoVehicleCanMove() {
        Board board = new Board(1, 2);
        Vehicle car = new Vehicle("C", 2, Orientation.horizontal, new Position(0, 0), false);
        board.addVehicle(car);
        Scanner input = new Scanner("show\n");

        new Game(new SavedGame(board, Turn.HUMAN), input, PLAYER, repository).start();

        assertNull(board.getWinner());
        assertEquals("show", input.nextLine());
        assertEquals(new Position(0, 0), car.getPosition());
    }

    @Test
    void shouldShowHighScoresWithoutGivingTheComputerATurn() {
        Board board = createBoard();
        PredictableComputer computer = new PredictableComputer();
        Scanner input = new Scanner("highscore extra\nhighscore\nquit\n");

        new Game(new SavedGame(board, Turn.HUMAN), input, computer, PLAYER, repository).start();

        verify(repository).showHighScores();
        verifyNoMoreInteractions(repository);
        assertEquals(0, computer.turnCount);
        assertEquals(new Position(2, 1), board.getVehicle("A").getPosition());
    }
    @Test
    void shouldPlaySavedComputerTurnBeforeHandlingQuit() {
        Board board = createBoard();
        PredictableComputer computer = new PredictableComputer();
        Scanner input = new Scanner("quit\nmove A 1\n");

        new Game(new SavedGame(board, Turn.COMPUTER), input, computer, PLAYER, repository)
                .start();

        assertEquals(1, computer.turnCount);
        assertEquals(new Position(1, 4), board.getVehicle("B").getPosition());
        assertEquals(new Position(2, 1), board.getVehicle("A").getPosition());
        assertEquals("move A 1", input.nextLine());
    }

    @Test
    void shouldSaveOnlyValidNamesWithoutConsumingTheTurn() {
        Board board = createBoard();
        PredictableComputer computer = new PredictableComputer();
        Scanner input = new Scanner("save\nsave two words\nsave ../outside\nsave slot_1-2\nquit\n");

        try (var factory = mockStatic(GameFactory.class)) {
            new Game(new SavedGame(board, Turn.HUMAN), input, computer, PLAYER, repository)
                    .start();

            factory.verify(() -> GameFactory.saveBoard(board, Turn.HUMAN, "saves/slot_1-2.txt"));
            factory.verifyNoMoreInteractions();
        }
        assertEquals(0, computer.turnCount);
    }

    @Test
    void shouldContinueAfterSaveFails() {
        Board board = createBoard();
        PredictableComputer computer = new PredictableComputer();
        Scanner input = new Scanner("save blocked\nmove A 1\nquit\n");
        try (var factory = mockStatic(GameFactory.class)) {
            factory.when(() -> GameFactory.saveBoard(board, Turn.HUMAN, "saves/blocked.txt"))
                    .thenThrow(new IOException("write failed"));

            new Game(new SavedGame(board, Turn.HUMAN), input, computer, PLAYER, repository)
                    .start();

            factory.verify(() -> GameFactory.saveBoard(board, Turn.HUMAN, "saves/blocked.txt"));
        }
        assertEquals(1, computer.turnCount);
        assertEquals(new Position(2, 2), board.getVehicle("A").getPosition());
    }
    @Test
    void shouldKeepHumanTurnAfterPartialExitAndEndOnCompleteExit() {
        Board board = new Board(1, 6);
        Vehicle hero = new Vehicle("A", 2, Orientation.horizontal, new Position(0, 4), true);
        assertTrue(board.addVehicle(hero));
        board.addExit(new Exit("A", 0, ExitDirection.RIGHT));
        PredictableComputer computer = new PredictableComputer();

        try (var input = new Scanner("move A 1\nmove A 2\nshow\n")) {
            new Game(new SavedGame(board, Turn.HUMAN), input, computer, PLAYER, repository)
                    .start();

            assertSame(hero, board.getWinner());
            assertEquals(0, computer.turnCount);
            assertEquals("show", input.nextLine());
            verify(repository).addWin(PLAYER);
        }
    }

    @Test
    void shouldSaveUpdatedBoardWithHumanTurnAfterComputerMoves() {
        Board board = createBoard();
        PredictableComputer computer = new PredictableComputer();

        try (var factory = mockStatic(GameFactory.class);
             var input = new Scanner("move A 1\nsave next_turn\nquit\n")) {
            factory.when(() -> GameFactory.saveBoard(board, Turn.HUMAN, "saves/next_turn.txt"))
                    .thenAnswer(call -> {
                        assertEquals(new Position(2, 2), board.getVehicle("A").getPosition());
                        assertEquals(new Position(1, 4), board.getVehicle("B").getPosition());
                        return null;
                    });

            new Game(new SavedGame(board, Turn.HUMAN), input, computer, PLAYER, repository)
                    .start();

            factory.verify(() -> GameFactory.saveBoard(board, Turn.HUMAN, "saves/next_turn.txt"));
            assertEquals(1, computer.turnCount);
        }
    }

    @Test
    void shouldReturnControlToHumanWhenComputerReturnsNoMove() {
        Board board = createBoard();
        ComputerPlayer computer = mock(ComputerPlayer.class);
        when(computer.hasLegalMove(board)).thenReturn(true);

        try (var input = new Scanner("move A 1\nquit\n")) {
            new Game(new SavedGame(board, Turn.COMPUTER), input, computer, PLAYER, repository)
                    .start();

            assertEquals(new Position(2, 2), board.getVehicle("A").getPosition());
            assertEquals(new Position(0, 4), board.getVehicle("B").getPosition());
            verify(computer, times(2)).chooseMove(board);
            verifyNoInteractions(repository);
        }
    }

    @Test
    void shouldEndGameWhenComputerHeroTakesVerticalExit() {
        Board board = new Board(2, 5);
        Vehicle hero = new Vehicle("B", 2, Orientation.vertical, new Position(0, 4), true);
        assertTrue(board.addVehicle(hero));
        board.addExit(new Exit("B", 4, ExitDirection.TOP));

        try (var input = new Scanner("show\n")) {
            new Game(new SavedGame(board, Turn.COMPUTER), input, PLAYER, repository).start();

            assertSame(hero, board.getWinner());
            assertEquals("show", input.nextLine());
            verifyNoInteractions(repository);
        }
    }
}
