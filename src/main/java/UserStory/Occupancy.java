package UserStory;

import lombok.AllArgsConstructor;
import lombok.Data;


/**
 * Data model representing one monthly occupancy record for a hotel.
 * The id links back to the hotel's id in hotels.txt.
 */
@Data
@AllArgsConstructor
public class Occupancy {

    private int id;
    private int rooms;
    private int usedRooms;
    private int beds;
    private int usedBeds;
    private int year;
    private int month;

}