package org.example.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class VehicleTest {
    @Test
    void shouldRejectInvalidLength() {
        assertThrows(IllegalArgumentException.class, () ->
                new Vehicle("A", 0, Orientation.horizontal, new Position(0, 0), false));
        assertThrows(IllegalArgumentException.class, () ->
                new Vehicle("A", -1, Orientation.horizontal, new Position(0, 0), false));
    }

    @Test
    void shouldRejectInvalidId() {
        String[] ids = {null, "", " ", "A B", "A\tB"};
        for (String id : ids) {
            assertThrows(IllegalArgumentException.class, () ->
                    new Vehicle(id, 2, Orientation.horizontal, new Position(0, 0), false));
        }
    }

    @Test
    void shouldRejectMissingOrientationOrPosition() {
        assertThrows(IllegalArgumentException.class, () ->
                new Vehicle("A", 2, null, new Position(0, 0), false));
        assertThrows(IllegalArgumentException.class, () ->
                new Vehicle("A", 2, Orientation.horizontal, null, false));
    }

    @Test
    void shouldKeepPositionWhenNullUpdateIsRejected() {
        Vehicle car = new Vehicle("A", 2, Orientation.horizontal, new Position(0, 0), true);

        assertThrows(IllegalArgumentException.class, () -> car.setPosition(null));

        assertEquals(new Position(0, 0), car.getPosition());
        assertTrue(car.isHero());
    }
}
