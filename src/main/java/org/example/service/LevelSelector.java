
package org.example.service;

import org.example.engine.GameFactory;
import org.example.model.Board;

import java.io.IOException;
import java.util.Scanner;

public class LevelSelector {

    public static Board selectBoard(Scanner scanner) {
        int levelCount = 6;
        Board board = null;

        while (board == null) {
            System.out.print(
                    "Choose a level (1-" + levelCount
                            + "), or 0 to load saved game: "
            );

            int level;

            try {
                level = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
                continue;
            }

            if (level < 0 || level > levelCount) {
                System.out.println("Invalid level. Please try again.");
                continue;
            }

            if (level == 0) {
                try {
                    board = GameFactory.loadBoardFromFile("save.txt");
                } catch (IOException | IllegalArgumentException e) {
                    System.out.println(
                            "Cannot load saved game: " + e.getMessage()
                    );
                }
            } else {
                String path = "list of level/level" + level + ".txt";
                board = GameFactory.createInitialBoard(path);
            }
        }

        return board;
    }
}
