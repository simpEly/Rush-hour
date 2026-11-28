package org.example.model;

/** Board edge, with the orientation and step sign required to reach it. */
public enum ExitDirection {
    LEFT(Orientation.horizontal, -1),
    RIGHT(Orientation.horizontal, 1),
    TOP(Orientation.vertical, -1),
    BOTTOM(Orientation.vertical, 1);

    private final Orientation orientation;
    private final int stepSign;

    ExitDirection(Orientation orientation, int stepSign) {
        this.orientation = orientation;
        this.stepSign = stepSign;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public int getStepSign() {
        return stepSign;
    }
}
