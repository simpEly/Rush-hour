package org.example.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mockStatic;

import java.io.IOException;
import java.util.Scanner;
import org.example.engine.GameFactory;
import org.example.model.SavedGame;
import org.example.model.Turn;
import org.junit.jupiter.api.Test;

class LevelSelectorTest {
    @Test
    void shouldRetryInvalidChoicesThenReturnSelectedLevel() throws IOException {
        SavedGame level = new SavedGame(GameFactory.createDefaultBoard(), Turn.HUMAN);
        try (var factory = mockStatic(GameFactory.class);
             var input = new Scanner("text\n-1\n7\n3\n")) {
            factory.when(() -> GameFactory.loadGameFromFile("List of level/level3.txt"))
                    .thenReturn(level);

            assertSame(level, LevelSelector.selectGame(input));
            factory.verify(() -> GameFactory.loadGameFromFile("List of level/level3.txt"));
            factory.verifyNoMoreInteractions();
        }
    }

    @Test
    void shouldRejectInvalidNameAndRetryMissingSaveBeforeLoadingNamedGame() throws IOException {
        SavedGame saved = new SavedGame(GameFactory.createDefaultBoard(), Turn.COMPUTER);
        try (var factory = mockStatic(GameFactory.class);
             var input = new Scanner("0\n../unsafe\n0\nmissing\n0\n slot_1-2 \n")) {
            factory.when(() -> GameFactory.loadGameFromFile("saves/missing.txt"))
                    .thenThrow(new IOException("missing save"));
            factory.when(() -> GameFactory.loadGameFromFile("saves/slot_1-2.txt"))
                    .thenReturn(saved);

            assertSame(saved, LevelSelector.selectGame(input));
            assertEquals(Turn.COMPUTER, saved.turn());
            factory.verify(() -> GameFactory.loadGameFromFile("saves/missing.txt"));
            factory.verify(() -> GameFactory.loadGameFromFile("saves/slot_1-2.txt"));
            factory.verifyNoMoreInteractions();
        }
    }

    @Test
    void shouldOfferDefaultAfterLevelFailsAndRejectInvalidFallbackChoice() {
        try (var factory = mockStatic(GameFactory.class);
             var input = new Scanner("1\ninvalid\n1\n")) {
            factory.when(() -> GameFactory.loadGameFromFile("List of level/level1.txt"))
                    .thenThrow(new IllegalArgumentException("invalid level"));
            factory.when(GameFactory::createDefaultBoard).thenCallRealMethod();

            SavedGame game = LevelSelector.selectGame(input);

            assertEquals(Turn.HUMAN, game.turn());
            assertEquals(6, game.board().getColumn());
            assertEquals(4, game.board().getVehicles().size());
        }
    }

    @Test
    void shouldAllowChoosingAnotherLevelAfterLoadFailure() {
        SavedGame level = new SavedGame(GameFactory.createDefaultBoard(), Turn.HUMAN);
        try (var factory = mockStatic(GameFactory.class);
             var input = new Scanner("1\n2\n6\n")) {
            factory.when(() -> GameFactory.loadGameFromFile("List of level/level1.txt"))
                    .thenThrow(new IOException("missing level"));
            factory.when(() -> GameFactory.loadGameFromFile("List of level/level6.txt"))
                    .thenReturn(level);

            assertSame(level, LevelSelector.selectGame(input));
            factory.verify(() -> GameFactory.loadGameFromFile("List of level/level1.txt"));
            factory.verify(() -> GameFactory.loadGameFromFile("List of level/level6.txt"));
            factory.verifyNoMoreInteractions();
        }
    }
}
