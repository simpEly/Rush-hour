package org.example.ui;

import java.util.Locale;
import org.example.model.Board;
import org.example.model.Exit;
import org.example.model.Orientation;
import org.example.model.Position;
import org.example.model.Vehicle;

public class BoardPrinter {
    public void print(Board board) {
        for (Exit exit : board.getExits()) {
            String side = exit.getDirection().name().toLowerCase(Locale.ROOT);
            String coordinate = exit.getDirection().getOrientation() == Orientation.vertical
                    ? "column" : "row";
            System.out.println("Exit " + exit.getVehicleId() + ": " + coordinate + " "
                    + exit.getCoordinate() + " (starting at 0), " + side);
        }
        for (int row = 0; row < board.getRow(); row++) {
            for (int column = 0; column < board.getColumn(); column++) {
                Vehicle vehicle = board.getVehicleAt(new Position(row, column));
                String cell = vehicle == null ? "." : vehicle.getId();
                if (column > 0) {
                    System.out.print(" ");
                }
                System.out.print(cell);
            }
            System.out.println();
        }
    }
}
