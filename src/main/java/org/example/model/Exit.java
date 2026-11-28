package org.example.model;

/** A hero's dedicated exit. The coordinate is a row for LEFT/RIGHT or a column for TOP/BOTTOM. */
public class Exit {
    private final String vehicleId;
    private final int coordinate;
    private final ExitDirection direction;

    public Exit(String vehicleId, int coordinate, ExitDirection direction) {
        if (vehicleId == null || vehicleId.isBlank() || coordinate < 0 || direction == null) {
            throw new IllegalArgumentException("Exit requires a vehicle ID, coordinate and direction");
        }
        this.vehicleId = vehicleId;
        this.coordinate = coordinate;
        this.direction = direction;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public int getCoordinate() {
        return coordinate;
    }

    public ExitDirection getDirection() {
        return direction;
    }
}
