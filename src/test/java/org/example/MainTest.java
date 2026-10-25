package org.example;

import org.example.model.Board;
import org.example.model.Position;
import org.example.model.Vehicle;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MainTest {
    @Test
    void shouldStartWithBothHeroesBlocked() {
        Board board = Main.createBoard();
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
        Board board = Main.createBoard();
        Vehicle hero = board.getVehicle("A");

        assertTrue(board.moveVehicle(board.getVehicle("C"), 3));
        assertNull(board.getWinner());
        assertTrue(board.moveVehicle(hero, 5));

        assertSame(hero, board.getWinner());
    }

    @Test
    void shouldHaveWinningPathForComputerHero() {
        Board board = Main.createBoard();
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
}
