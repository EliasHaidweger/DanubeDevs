package model;

import lombok.Data;

/**
 * Repraesentiert eine Zeile aus dbo.occupancies.
 * Primaerschluessel: hotel_id + year + month
 */
@Data
public class Occupancy {

    private int hotelId;
    private int rooms;
    private int usedRooms;
    private int beds;
    private int usedBeds;
    private int year;
    private int month;

    public double getRoomOccupancyPercent() {
        if (rooms == 0) return 0;
        return (usedRooms * 100.0) / rooms;
    }

    public double getBedOccupancyPercent() {
        if (beds == 0) return 0;
        return (usedBeds * 100.0) / beds;
    }
}
