package org.example.ui;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.example.model.Board;
import org.example.model.Exit;
import org.example.model.ExitDirection;
import org.example.model.Orientation;
import org.example.model.Position;
import org.example.model.Vehicle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;
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

    @Test
    @ResourceLock(Resources.SYSTEM_OUT)
    void shouldLabelAllExitDirectionsWithTheCorrectCoordinateAxis() {
        Board board = new Board(4, 7);
        assertTrue(board.addVehicle(new Vehicle("A", 2, Orientation.horizontal, new Position(0, 0), true)));
        assertTrue(board.addVehicle(new Vehicle("B", 2, Orientation.horizontal, new Position(3, 0), true)));
        assertTrue(board.addVehicle(new Vehicle("C", 2, Orientation.vertical, new Position(0, 4), true)));
        assertTrue(board.addVehicle(new Vehicle("D", 2, Orientation.vertical, new Position(2, 6), true)));
        board.addExit(new Exit("A", 0, ExitDirection.LEFT));
        board.addExit(new Exit("B", 3, ExitDirection.RIGHT));
        board.addExit(new Exit("C", 4, ExitDirection.TOP));
        board.addExit(new Exit("D", 6, ExitDirection.BOTTOM));

        PrintStream original = System.out;
        var output = new ByteArrayOutputStream();
        try (var capture = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setOut(capture);
            new BoardPrinter().print(board);
        } finally {
            System.setOut(original);
        }

        var lines = output.toString(StandardCharsets.UTF_8).lines().toList();
        assertEquals("Exit A: row 0 (starting at 0), left", lines.get(0));
        assertEquals("Exit B: row 3 (starting at 0), right", lines.get(1));
        assertEquals("Exit C: column 4 (starting at 0), top", lines.get(2));
        assertEquals("Exit D: column 6 (starting at 0), bottom", lines.get(3));
    }
}
