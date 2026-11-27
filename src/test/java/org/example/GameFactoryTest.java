package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.example.engine.GameFactory;
import org.example.model.Board;
import org.example.model.Exit;
import org.example.model.Position;
import org.example.model.Vehicle;
import org.example.model.SavedGame;
import org.example.model.Turn;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class GameFactoryTest {
    @TempDir
    Path tempDir;

    @Test
    void shouldStartWithBothHeroesBlocked() {
        Board board = GameFactory.createDefaultBoard();
        assertEquals(4, board.getVehicles().size());
        assertEquals(2, board.getExits().size());
        assertFalse(board.canMoveVehicle(board.getVehicle("A"), 5));
        assertFalse(board.canMoveVehicle(board.getVehicle("B"), -4));
        assertNull(board.getWinner());
        assertEquals(new Position(2, 1), board.getVehicle("A").getPosition());
        assertEquals(new Position(4, 2), board.getVehicle("B").getPosition());
    }

    @Test
    void shouldHaveWinningPathForHumanHero() {
        Board board = GameFactory.createDefaultBoard();
        Vehicle hero = board.getVehicle("A");

        assertTrue(board.moveVehicle(board.getVehicle("C"), 3));
        assertNull(board.getWinner());
        assertTrue(board.moveVehicle(hero, 5));

        assertSame(hero, board.getWinner());
    }

    @Test
    void shouldHaveWinningPathForComputerHero() {
        Board board = GameFactory.createDefaultBoard();
        Vehicle hero = board.getVehicle("B");

        // A initially prevents D from moving up to clear B's exit.
        assertFalse(board.moveVehicle(board.getVehicle("D"), -2));
        assertTrue(board.moveVehicle(board.getVehicle("C"), 3));
        assertTrue(board.moveVehicle(board.getVehicle("A"), 1));
        assertTrue(board.moveVehicle(board.getVehicle("D"), -2));
        assertNull(board.getWinner());
        assertTrue(board.moveVehicle(hero, -4));

        assertSame(hero, board.getWinner());
    }

    @ParameterizedTest
    @EnumSource(Turn.class)
    void shouldRoundTripBoardAfterAMove(Turn turn) throws IOException {
        Board original = GameFactory.createDefaultBoard();
        assertTrue(original.moveVehicle(original.getVehicle("C"), 3));
        Path save = tempDir.resolve("saves/player_one.txt");

        GameFactory.saveBoard(original, turn, save.toString());
        SavedGame loaded = GameFactory.loadGameFromFile(save.toString());
        assertEquals(turn, loaded.turn());
        Board restored = loaded.board();

        assertEquals(original.getRow(), restored.getRow());
        assertEquals(original.getColumn(), restored.getColumn());
        assertEquals(original.getVehicles().size(), restored.getVehicles().size());
        for (Vehicle expected : original.getVehicles()) {
            Vehicle actual = restored.getVehicle(expected.getId());
            assertNotNull(actual, expected.getId());
            assertEquals(expected.getPosition(), actual.getPosition());
            assertEquals(expected.getLength(), actual.getLength());
            assertEquals(expected.getOrientation(), actual.getOrientation());
            assertEquals(expected.isHero(), actual.isHero());
        }
        assertEquals(original.getExits().size(), restored.getExits().size());
        for (Exit expected : original.getExits()) {
            Exit actual = restored.getExits().stream()
                    .filter(exit -> exit.getVehicleId().equals(expected.getVehicleId()))
                    .findFirst().orElseThrow();
            assertEquals(expected.getRow(), actual.getRow());
            assertEquals(expected.isRightSide(), actual.isRightSide());
        }
        assertTrue(restored.moveVehicle(restored.getVehicle("A"), 5));
        assertEquals("A", restored.getWinner().getId());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "",
        "BOARD 0 6",
        "BOARD 6 6\nBOARD 6 6",
        "BOARD 6 6\nVEHICLE A 2 X 2 1 true",
        "BOARD 6 6\nVEHICLE A 2 H 2 1 maybe",
        "BOARD 6 6\nVEHICLE A 2 H 2 1 true\nVEHICLE B 2 H 2 2 true",
        "BOARD 6 6\nEXIT A 2 UP",
        "BOARD 6 6\nTURN OTHER",
        "BOARD 6 6\nTURN HUMAN\nTURN COMPUTER",
        "BOARD 6 6\nTURN HUMAN\nSCORE 42",
        "BOARD 6 6\nTURN HUMAN\nVEHICLE A 2 H 2 1 true\nEXIT A 2 RIGHT",
        "BOARD 6 6\nTURN HUMAN\nVEHICLE A 2 H 2 1 true\nVEHICLE B 2 H 4 2 true\nEXIT A 2 RIGHT"
    })
    void shouldRejectInvalidBoardFile(String content) throws IOException {
        Path file = Files.writeString(tempDir.resolve("invalid.txt"), content);

        assertThrows(IllegalArgumentException.class,
                () -> GameFactory.loadGameFromFile(file.toString()));
    }

    @Test
    void shouldRejectOtherwiseValidBoardWithoutTurn() throws IOException {
        Path save = tempDir.resolve("missing-turn.txt");
        GameFactory.saveBoard(GameFactory.createDefaultBoard(), Turn.HUMAN, save.toString());
        Files.writeString(save, Files.readString(save).replace("TURN HUMAN\n", ""));

        var error = assertThrows(IllegalArgumentException.class,
                () -> GameFactory.loadGameFromFile(save.toString()));
        assertEquals("Missing TURN", error.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6})
    void shouldReadBundledLevelWithHumanTurn(int level) throws IOException {
        Path file = Path.of("List of level", "level" + level + ".txt");
        SavedGame game = GameFactory.loadGameFromFile(file.toString());

        assertEquals(Turn.HUMAN, game.turn());
        assertEquals(12, game.board().getColumn());
        assertEquals(2, game.board().getExits().size());
    }
}
