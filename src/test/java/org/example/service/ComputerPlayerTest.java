package org.example.service;

import java.util.Random;
import org.example.model.Board;
import org.example.model.Exit;
import org.example.model.Move;
import org.example.model.Orientation;
import org.example.model.Position;
import org.example.model.Vehicle;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ComputerPlayerTest {
    @Test
    void shouldReturnNullWhenAllVehiclesAreBlocked() {
        Board board = new Board(1, 3);
        board.addVehicle(new Vehicle("C", 2, Orientation.horizontal, new Position(0, 0), false));
        board.addVehicle(new Vehicle("D", 1, Orientation.horizontal, new Position(0, 2), false));
        ComputerPlayer computer = new ComputerPlayer(new Random(1));

        assertFalse(computer.hasLegalMove(board));
        assertNull(computer.chooseMove(board));
    }

    @Test
    void shouldChooseRightExitWithoutChangingBoardDuringSelection() {
        Board board = new Board(1, 2);
        Vehicle hero = new Vehicle("A", 2, Orientation.horizontal, new Position(0, 0), true);
        board.addVehicle(hero);
        board.addExit(new Exit("A", 0, true));

        Move move = new ComputerPlayer(new Random(1)).chooseMove(board);

        assertNotNull(move);
        assertSame(hero, move.getVehicle());
        assertEquals(2, move.getSteps());
        assertNull(board.getWinner());
        assertSame(hero, board.getVehicle("A"));
        assertEquals(new Position(0, 0), hero.getPosition());
        assertTrue(board.moveVehicle(move.getVehicle(), move.getSteps()));
        assertSame(hero, board.getWinner());
    }

    @Test
    void shouldChooseLeftExit() {
        Board board = new Board(1, 2);
        Vehicle hero = new Vehicle("B", 2, Orientation.horizontal, new Position(0, 0), true);
        board.addVehicle(hero);
        board.addExit(new Exit("B", 0, false));

        Move move = new ComputerPlayer(new Random(2)).chooseMove(board);

        assertNotNull(move);
        assertEquals(-2, move.getSteps());
        assertTrue(board.moveVehicle(move.getVehicle(), move.getSteps()));
        assertSame(hero, board.getWinner());
    }

    @Test
    void shouldChooseVerticalMove() {
        Board board = new Board(3, 1);
        Vehicle car = new Vehicle("C", 2, Orientation.vertical, new Position(0, 0), false);
        board.addVehicle(car);

        Move move = new ComputerPlayer(new Random(3)).chooseMove(board);

        assertSame(car, move.getVehicle());
        assertEquals(1, move.getSteps());
        assertEquals(new Position(0, 0), car.getPosition());
        assertTrue(board.moveVehicle(car, move.getSteps()));
        assertEquals(new Position(1, 0), car.getPosition());
    }

    @Test
    void shouldSelectBothMovableVehiclesButNeverBlockedVehicle() {
        Board board = new Board(3, 6);
        Vehicle a = new Vehicle("A", 2, Orientation.horizontal, new Position(0, 0), true);
        Vehicle b = new Vehicle("B", 2, Orientation.horizontal, new Position(1, 1), true);
        Vehicle blocked = new Vehicle("C", 6, Orientation.horizontal, new Position(2, 0), false);
        board.addVehicle(a);
        board.addVehicle(b);
        board.addVehicle(blocked);
        ComputerPlayer computer = new ComputerPlayer(new Random(42));
        boolean selectedA = false;
        boolean selectedB = false;
        boolean selectedLongMove = false;

        for (int i = 0; i < 100; i++) {
            Move move = computer.chooseMove(board);
            assertNotNull(move);
            assertNotSame(blocked, move.getVehicle());
            assertTrue(board.canMoveVehicle(move.getVehicle(), move.getSteps()));
            selectedA = selectedA || move.getVehicle() == a;
            selectedB = selectedB || move.getVehicle() == b;
            selectedLongMove = selectedLongMove || Math.abs(move.getSteps()) > 1;
        }
        assertTrue(selectedA);
        assertTrue(selectedB);
        assertTrue(selectedLongMove);
        assertEquals(new Position(0, 0), a.getPosition());
        assertEquals(new Position(1, 1), b.getPosition());
        assertNull(board.getWinner());
    }

    @Test
    void shouldHaveNoMovesAfterWinnerHasEscaped() {
        Board board = new Board(1, 2);
        Vehicle hero = new Vehicle("A", 2, Orientation.horizontal, new Position(0, 0), true);
        board.addVehicle(hero);
        board.addExit(new Exit("A", 0, true));
        assertTrue(board.moveVehicle(hero, 2));

        ComputerPlayer computer = new ComputerPlayer();
        assertNull(computer.chooseMove(board));
        assertFalse(computer.hasLegalMove(board));
    }
}
