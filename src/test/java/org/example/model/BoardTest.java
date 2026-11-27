package org.example.model;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {

    @Test
    void shouldAddVehicleWhenValid() {
        Board board = new Board(6, 6);

        Vehicle car = new Vehicle(
                "A",
                2,
                Orientation.horizontal,
                new Position(2, 1),
                false
        );
        assertTrue(board.addVehicle(car));
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 6; col++) {
                boolean expected = row == 2 && (col == 2 || col == 1);
                assertEquals(expected, board.isOccupied(new Position(row, col)));
            }
        }
    }

    @Test
    void shouldMoveHorizontalVehicleLeft() {
        Board board = new Board(6, 6);
        Vehicle car = new Vehicle(
                "A", 2, Orientation.horizontal, new Position(2, 2), false
        );
        assertTrue(board.addVehicle(car));

        assertTrue(board.moveVehicle(car, -1));

        assertEquals(new Position(2, 1), car.getPosition());
        assertTrue(board.isOccupied(new Position(2, 1)));
        assertTrue(board.isOccupied(new Position(2, 2)));
        assertFalse(board.isOccupied(new Position(2, 3)));
    }

    @Test
    void shouldMoveVerticalVehicleDown() {
        Board board = new Board(6, 6);
        Vehicle car = new Vehicle(
                "A", 2, Orientation.vertical, new Position(1, 2), false
        );
        assertTrue(board.addVehicle(car));

        assertTrue(board.moveVehicle(car, 1));

        assertEquals(new Position(2, 2), car.getPosition());
        assertFalse(board.isOccupied(new Position(1, 2)));
        assertTrue(board.isOccupied(new Position(2, 2)));
        assertTrue(board.isOccupied(new Position(3, 2)));
    }

    @Test
    void shouldMoveVerticalVehicleUp() {
        Board board = new Board(6, 6);
        Vehicle car = new Vehicle(
                "A", 2, Orientation.vertical, new Position(2, 2), false
        );
        assertTrue(board.addVehicle(car));

        assertTrue(board.moveVehicle(car, -1));

        assertEquals(new Position(1, 2), car.getPosition());
        assertTrue(board.isOccupied(new Position(1, 2)));
        assertTrue(board.isOccupied(new Position(2, 2)));
        assertFalse(board.isOccupied(new Position(3, 2)));
    }

    @Test
    void shouldRejectMoveOutsideBoard() {
        Board board = new Board(6, 6);
        Vehicle car = new Vehicle(
                "A", 2, Orientation.horizontal, new Position(2, 4), false
        );
        assertTrue(board.addVehicle(car));

        assertFalse(board.moveVehicle(car, 1));

        assertEquals(new Position(2, 4), car.getPosition());
        assertTrue(board.isOccupied(new Position(2, 4)));
        assertTrue(board.isOccupied(new Position(2, 5)));
    }

    @Test
    void shouldRejectMoveWhenAnotherVehicleBlocksPath() {
        Board board = new Board(6, 6);
        Vehicle car = new Vehicle(
                "A", 2, Orientation.horizontal, new Position(2, 0), false
        );
        Vehicle blocker = new Vehicle(
                "B", 2, Orientation.vertical, new Position(1, 3), false
        );
        assertTrue(board.addVehicle(car));
        assertTrue(board.addVehicle(blocker));

        // The destination is empty, but the path crosses the blocker.
        assertFalse(board.moveVehicle(car, 4));

        assertEquals(new Position(2, 0), car.getPosition());
        assertEquals(new Position(1, 3), blocker.getPosition());
        assertTrue(board.isOccupied(new Position(2, 0)));
        assertTrue(board.isOccupied(new Position(2, 1)));
        assertFalse(board.isOccupied(new Position(2, 4)));
        assertFalse(board.isOccupied(new Position(2, 5)));
    }

    @Test
    void shouldRejectMoveWhenVehicleIsNotOnBoard() {
        Board board = new Board(6, 6);
        Vehicle car = new Vehicle(
                "A", 2, Orientation.horizontal, new Position(2, 1), false
        );

        assertFalse(board.moveVehicle(car, 1));

        assertEquals(new Position(2, 1), car.getPosition());
        assertFalse(board.isOccupied(new Position(2, 1)));
        assertFalse(board.isOccupied(new Position(2, 2)));
    }

    @Test
    void shouldRejectMoveWhenStepsAreZero() {
        Board board = new Board(6, 6);
        Vehicle car = new Vehicle(
                "A", 2, Orientation.horizontal, new Position(2, 1), false
        );
        assertTrue(board.addVehicle(car));

        assertFalse(board.moveVehicle(car, 0));

        assertEquals(new Position(2, 1), car.getPosition());
        assertTrue(board.isOccupied(new Position(2, 1)));
        assertTrue(board.isOccupied(new Position(2, 2)));
    }

    @Test
    void shouldRejectInvalidBoardDimensions() {
        assertThrows(IllegalArgumentException.class, () -> new Board(0, 6));
        assertThrows(IllegalArgumentException.class, () -> new Board(6, -1));
    }

    @Test
    void shouldRejectDuplicateVehicleId() {
        Board board = new Board(6, 6);
        Vehicle first = new Vehicle("A", 2, Orientation.horizontal, new Position(0, 0), false);
        Vehicle duplicate = new Vehicle("A", 2, Orientation.horizontal, new Position(2, 0), false);

        assertTrue(board.addVehicle(first));
        assertFalse(board.addVehicle(duplicate));
        assertSame(first, board.getVehicle("A"));
        assertNull(board.getVehicleAt(new Position(2, 0)));
    }

    @Test
    void shouldRejectNullVehicle() {
        Board board = new Board(6, 6);
        assertFalse(board.addVehicle(null));
        assertFalse(board.moveVehicle(null, 1));
    }

    @Test
    void shouldRejectOverlappingAndOutsideVehicles() {
        Board board = new Board(6, 6);
        Vehicle first = new Vehicle("A", 2, Orientation.horizontal, new Position(2, 1), false);
        Vehicle overlap = new Vehicle("B", 2, Orientation.vertical, new Position(1, 2), false);
        Vehicle outside = new Vehicle("C", 2, Orientation.horizontal, new Position(0, 5), false);

        assertTrue(board.addVehicle(first));
        assertFalse(board.addVehicle(overlap));
        assertFalse(board.addVehicle(outside));
        assertNull(board.getVehicle("B"));
        assertNull(board.getVehicle("C"));
    }

    @Test
    void shouldAcceptLastCellAndRejectPositionsBeyondEachEdge() {
        Board board = new Board(3, 5);

        assertTrue(board.isInside(new Position(2, 4)));
        assertFalse(board.isInside(new Position(3, 4)));
        assertFalse(board.isInside(new Position(2, 5)));
        assertFalse(board.isInside(new Position(-1, 4)));
        assertFalse(board.isInside(new Position(2, -1)));
    }

    @Test
    void shouldRejectVerticalMovesThatCrossAnOccupiedCellInEitherDirection() {
        Board board = new Board(7, 3);
        Vehicle top = new Vehicle("T", 2, Orientation.vertical, new Position(0, 1), false);
        Vehicle bottom = new Vehicle("B", 2, Orientation.vertical, new Position(5, 1), false);
        Vehicle blocker = new Vehicle("C", 2, Orientation.horizontal, new Position(3, 0), false);
        assertTrue(board.addVehicle(top));
        assertTrue(board.addVehicle(bottom));
        assertTrue(board.addVehicle(blocker));

        assertFalse(board.moveVehicle(top, 4));
        assertFalse(board.moveVehicle(bottom, -4));
        assertEquals(new Position(0, 1), top.getPosition());
        assertEquals(new Position(5, 1), bottom.getPosition());
        assertSame(blocker, board.getVehicleAt(new Position(3, 1)));
    }

    @Test
    void shouldRejectMovementOfADifferentInstanceWithTheSameId() {
        Board board = new Board(3, 5);
        Position start = new Position(1, 1);
        Vehicle registered = new Vehicle("A", 2, Orientation.horizontal, start, false);
        Vehicle duplicate = new Vehicle("A", 2, Orientation.horizontal, start, false);
        assertTrue(board.addVehicle(registered));

        assertFalse(board.canMoveVehicle(duplicate, 1));
        assertFalse(board.moveVehicle(duplicate, 1));
        assertTrue(board.canMoveVehicle(registered, 1));
        assertEquals(start, registered.getPosition());
        assertEquals(start, duplicate.getPosition());
    }
}
