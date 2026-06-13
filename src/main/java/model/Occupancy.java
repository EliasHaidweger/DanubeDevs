package model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Hibernate entity for the occupancies table.
 *
 * Special feature: composite primary key (hotel_id + year + month).
 * That's why @IdClass(OccupancyId.class) and 3 fields with @Id.
 */
@Entity
@Table(name = "occupancies")
@IdClass(OccupancyId.class)
@Data
public class Occupancy {

    @Id
    @Column(name = "hotel_id")
    private int hotelId;

    @Id
    @Column(name = "year")
    private int year;

    @Id
    @Column(name = "month")
    private int month;

    @Column(name = "rooms")
    private int rooms;

    @Column(name = "usedrooms")
    private int usedRooms;

    @Column(name = "beds")
    private int beds;

    @Column(name = "usedbeds")
    private int usedBeds;

    public double getRoomOccupancyPercent() {
        if (rooms == 0) return 0;
        return (usedRooms * 100.0) / rooms;
    }

    public double getBedOccupancyPercent() {
        if (beds == 0) return 0;
        return (usedBeds * 100.0) / beds;
    }
}