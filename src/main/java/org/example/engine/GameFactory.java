package org.example.engine;


import org.example.model.*;

import java.util.Random;

public class GameFactory {
    public static Board createRandomBoard(int rows, int columns) {
        if (rows < 3 || columns < 4) {
            throw new IllegalArgumentException(
                    "Board must have at least 3 rows and 4 columns."
            );
        }

        Board board = new Board(rows, columns);

        Random random = new Random();
        int rowA = random.nextInt(rows);
        int rowB;
        do {
            rowB = random.nextInt(rows);
        } while (rowA == rowB);

        int columnA = random.nextInt(columns-1);
        int columnB = random.nextInt(columns-1);

        Position positionA = new Position(rowA, columnA);
        Position positionB = new Position(rowB, columnB);

        Vehicle carA = new Vehicle("A",2,Orientation.horizontal,positionA,true);
        Vehicle carB = new Vehicle("B",2,Orientation.horizontal,positionB,true);
        board.addVehicle(carA);
        board.addVehicle(carB);

        Exit exitA = new Exit("A",rowA,true);
        Exit exitB = new Exit("B",rowB,false);
        board.addExit(exitA);
        board.addExit(exitB);

        return board;
    }

}
