package model;

public class Occupancy {
    public int hotelId;
    public Integer rooms;
    public Integer usedRooms;
    public Integer beds;
    public Integer usedBeds;
    public int year;
    public int month;

    public Occupancy(int hotelId, Integer rooms, Integer usedRooms, Integer beds, Integer usedBeds, int year, int month) {
        this.hotelId = hotelId;
        this.rooms = rooms;
        this.usedRooms = usedRooms;
        this.beds = beds;
        this.usedBeds = usedBeds;
        this.year = year;
        this.month = month;
    }
}
