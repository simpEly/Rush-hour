package org.example;

import org.example.model.Board;
import org.example.model.Exit;
import org.example.model.Orientation;
import org.example.model.Position;
import org.example.model.Vehicle;
import org.example.service.Game;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Board board = createBoard();
        Game game = new Game(board, new Scanner(System.in));
        game.start();
    }

    public static Board createBoard() {
        Board board = new Board(6, 6);
        board.addVehicle(new Vehicle(
                "A", 2, Orientation.horizontal, new Position(2, 1), true
        ));
        board.addVehicle(new Vehicle(
                "B", 2, Orientation.horizontal, new Position(4, 2), true
        ));
        board.addVehicle(new Vehicle(
                "C", 3, Orientation.vertical, new Position(0, 4), false
        ));
        board.addVehicle(new Vehicle(
                "D", 3, Orientation.vertical, new Position(3, 1), false
        ));
        board.addExit(new Exit("A", 2, true));
        board.addExit(new Exit("B", 4, false));
        return board;
    }
}

