package org.example.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ExitTest {
    private Board createBoard() {
        Board board = new Board(6, 6);
        board.addVehicle(new Vehicle("A", 2, Orientation.horizontal, new Position(2, 1), true));
        board.addExit(new Exit("A", 2, true));
        return board;
    }

    @Test
    void shouldWinOnlyAfterEntireHeroLeavesRightExit() {
        Board board = createBoard();
        Vehicle hero = board.getVehicle("A");

        assertTrue(board.moveVehicle(hero, 3));
        assertEquals(new Position(2, 4), hero.getPosition());
        assertNull(board.getWinner());

        assertFalse(board.moveVehicle(hero, 1));
        assertEquals(new Position(2, 4), hero.getPosition());
        assertNull(board.getWinner());

        assertTrue(board.moveVehicle(hero, 2));
        assertSame(hero, board.getWinner());
        assertNull(board.getVehicle("A"));
        assertFalse(board.isOccupied(new Position(2, 4)));
        assertFalse(board.isOccupied(new Position(2, 5)));
    }

    @Test
    void shouldAllowLongMoveThroughClearExit() {
        Board board = createBoard();
        Vehicle hero = board.getVehicle("A");
        assertTrue(board.moveVehicle(hero, 5));
        assertSame(hero, board.getWinner());
    }

    @Test
    void shouldAllowDedicatedLeftExitForOtherHero() {
        Board board = createBoard();
        Vehicle otherHero = new Vehicle("B", 2, Orientation.horizontal, new Position(4, 3), true);
        assertTrue(board.addVehicle(otherHero));
        board.addExit(new Exit("B", 4, false));

        assertFalse(board.moveVehicle(otherHero, 3));
        assertNull(board.getWinner());
        assertTrue(board.moveVehicle(otherHero, -5));
        assertSame(otherHero, board.getWinner());
        assertNotNull(board.getVehicle("A"));
    }

    @Test
    void shouldRejectExitThroughWrongSide() {
        Board board = createBoard();
        Vehicle hero = board.getVehicle("A");
        assertFalse(board.moveVehicle(hero, -3));
        assertEquals(new Position(2, 1), hero.getPosition());
        assertNull(board.getWinner());
    }

    @Test
    void shouldRejectEscapeWhenPathIsBlocked() {
        Board board = createBoard();
        Vehicle hero = board.getVehicle("A");
        Vehicle blocker = new Vehicle("C", 3, Orientation.vertical, new Position(0, 4), false);
        assertTrue(board.addVehicle(blocker));

        assertFalse(board.moveVehicle(hero, 5));
        assertNull(board.getWinner());
        assertEquals(new Position(2, 1), hero.getPosition());
        assertSame(blocker, board.getVehicle("C"));
    }

    @Test
    void shouldRejectBlockedLeftExit() {
        Board board = new Board(6, 6);
        Vehicle hero = new Vehicle("B", 2, Orientation.horizontal, new Position(2, 3), true);
        board.addVehicle(hero);
        board.addVehicle(new Vehicle("C", 2, Orientation.vertical, new Position(1, 1), false));
        board.addExit(new Exit("B", 2, false));

        assertFalse(board.moveVehicle(hero, -5));
        assertEquals(new Position(2, 3), hero.getPosition());
        assertNull(board.getWinner());
    }

    @Test
    void shouldNotAllowOtherVehiclesToUseHeroExit() {
        Board board = createBoard();
        Vehicle hero = board.getVehicle("A");
        assertTrue(board.moveVehicle(hero, -1));
        Vehicle ordinary = new Vehicle("C", 2, Orientation.horizontal, new Position(2, 3), false);
        assertTrue(board.addVehicle(ordinary));

        assertFalse(board.moveVehicle(ordinary, 3));
        assertEquals(new Position(2, 3), ordinary.getPosition());
        assertNull(board.getWinner());
    }

    @Test
    void shouldNotEscapeWithoutConfiguredExit() {
        Board board = new Board(6, 6);
        Vehicle hero = new Vehicle("A", 2, Orientation.horizontal, new Position(2, 1), true);
        board.addVehicle(hero);
        assertFalse(board.moveVehicle(hero, 5));
        assertNull(board.getWinner());
    }

    @Test
    void shouldRejectInvalidExitConfiguration() {
        Board board = createBoard();
        assertThrows(IllegalArgumentException.class, () -> board.addExit(null));
        assertThrows(IllegalArgumentException.class, () -> board.addExit(new Exit("A", 2, false)));
        assertThrows(IllegalArgumentException.class, () -> board.addExit(new Exit("A", 1, true)));
        assertThrows(IllegalArgumentException.class, () -> board.addExit(new Exit("A", 6, true)));
        assertThrows(IllegalArgumentException.class, () -> board.addExit(new Exit("missing", 2, true)));
        assertThrows(IllegalArgumentException.class, () -> new Exit("A", -1, true));
        assertThrows(IllegalArgumentException.class, () -> new Exit("", 2, true));
        assertThrows(IllegalArgumentException.class, () -> new Exit(null, 2, true));
    }

    @Test
    void shouldRejectExitForOrdinaryOrVerticalVehicle() {
        Board board = new Board(6, 6);
        board.addVehicle(new Vehicle("C", 2, Orientation.horizontal, new Position(0, 0), false));
        board.addVehicle(new Vehicle("D", 2, Orientation.vertical, new Position(2, 2), true));
        assertThrows(IllegalArgumentException.class, () -> board.addExit(new Exit("C", 0, true)));
        assertThrows(IllegalArgumentException.class, () -> board.addExit(new Exit("D", 2, true)));
    }

    @Test
    void shouldRejectFurtherMovesAfterWinning() {
        Board board = createBoard();
        Vehicle hero = board.getVehicle("A");
        Vehicle other = new Vehicle("C", 2, Orientation.horizontal, new Position(0, 0), false);
        board.addVehicle(other);
        assertTrue(board.moveVehicle(hero, 5));

        assertFalse(board.moveVehicle(other, 1));
        assertEquals(new Position(0, 0), other.getPosition());
        assertFalse(board.addVehicle(new Vehicle("D", 2, Orientation.horizontal, new Position(4, 0), false)));
        assertSame(hero, board.getWinner());
    }
}
