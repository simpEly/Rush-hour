package org.example.model;

/** A dedicated left or right exit for a horizontal hero vehicle. */
public class Exit {
    private final String vehicleId;
    private final int row;
    private final boolean rightSide;

    public Exit(String vehicleId, int row, boolean rightSide) {
        if (vehicleId == null || vehicleId.isBlank() || row < 0) {
            throw new IllegalArgumentException("Exit requires a vehicle ID and a valid row");
        }
        this.vehicleId = vehicleId;
        this.row = row;
        this.rightSide = rightSide;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public int getRow() {
        return row;
    }

    public boolean isRightSide() {
        return rightSide;
    }
}
