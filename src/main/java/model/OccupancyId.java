package model;

import lombok.Data;

import java.io.Serializable;

/**
 * Composite primary key for Occupancy.
 * The `occupancies` table has a PRIMARY KEY (hotel_id, year, month),
 * So we need a separate class to group these three fields together.
 *
 * Must be Serializable and have equals and hashCode methods (Lombok handles this with @Data).
 * IMPORTANT: The field names must exactly match the @Id fields in Occupancy.
 */
@Data
public class OccupancyId implements Serializable {

    private int hotelId;
    private int year;
    private int month;
}