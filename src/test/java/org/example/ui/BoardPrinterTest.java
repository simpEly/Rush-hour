package org.example.ui;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.example.model.Board;
import org.example.model.Orientation;
import org.example.model.Position;
import org.example.model.Vehicle;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BoardPrinterTest {
    private String render(Board board) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        new BoardPrinter().print(board, new PrintStream(output, true, StandardCharsets.UTF_8));
        return output.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
    }

    @Test
    void shouldPrintEmptyRectangularBoard() {
        assertEquals(". . .\n. . .\n", render(new Board(2, 3)));
    }

    @Test
    void shouldPrintVehiclesAndReflectMovement() {
        Board board = new Board(3, 5);
        Vehicle car = new Vehicle("A", 2, Orientation.horizontal, new Position(1, 0), true);
        Vehicle blocker = new Vehicle("B", 3, Orientation.vertical, new Position(0, 4), false);
        assertTrue(board.addVehicle(car));
        assertTrue(board.addVehicle(blocker));

        assertEquals(". . . . B\nA A . . B\n. . . . B\n", render(board));
        assertSame(car, board.getVehicleAt(new Position(1, 0)));
        assertNull(board.getVehicleAt(new Position(0, 0)));

        assertTrue(board.moveVehicle(car, 1));
        assertEquals(". . . . B\n. A A . B\n. . . . B\n", render(board));
    }
}
