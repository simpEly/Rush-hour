
package org.example;

import org.example.model.Board;
import org.example.service.Game;
import org.example.service.LevelSelector;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Board board = LevelSelector.selectBoard(scanner);

        Game game = new Game(board, scanner);
        game.start();
    }
}
