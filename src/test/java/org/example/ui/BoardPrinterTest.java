package org.example.ui;

import org.example.model.Board;
import org.example.model.Orientation;
import org.example.model.Position;
import org.example.model.Vehicle;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BoardPrinterTest {
    @Test
    void shouldReflectMovement() {
        Board board = new Board(3, 5);

        Vehicle car = new Vehicle(
                "A", 2, Orientation.horizontal,
                new Position(1, 0), true
        );

        Vehicle blocker = new Vehicle(
                "B", 3, Orientation.vertical,
                new Position(0, 4), false
        );

        assertTrue(board.addVehicle(car));
        assertTrue(board.addVehicle(blocker));

        BoardPrinter printer = new BoardPrinter();


        printer.print(board);

        assertSame(car, board.getVehicleAt(new Position(1, 0)));
        assertNull(board.getVehicleAt(new Position(0, 0)));


        assertTrue(board.moveVehicle(car, 1));


        printer.print(board);

        assertEquals(new Position(1, 1), car.getPosition());
        assertSame(car, board.getVehicleAt(new Position(1, 2)));
        assertNull(board.getVehicleAt(new Position(1, 0)));
    }
}
