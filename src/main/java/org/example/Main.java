package org.example;

import org.example.model.Board;
import org.example.model.Orientation;
import org.example.model.Position;
import org.example.model.Vehicle;
import org.example.ui.BoardPrinter;

public class Main {
    public static void main(String[] args) {
        Board board = new Board(6, 6);
        board.addVehicle(new Vehicle(
                "A", 2, Orientation.horizontal, new Position(2, 1), true
        ));
        board.addVehicle(new Vehicle(
                "B", 3, Orientation.vertical, new Position(0, 4), false
        ));
        new BoardPrinter().print(board, System.out);
    }
}

