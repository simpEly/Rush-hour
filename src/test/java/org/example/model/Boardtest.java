package org.example.model;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class Boardtest {

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
}