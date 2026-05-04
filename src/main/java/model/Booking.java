package model;

import java.sql.Date;

public class Booking {
    public int id;
    public int hotelId;
    public Date checkinDate;
    public int roomsOccupied;
    public int bedsOccupied;

    public Booking(int id, int hotelId, Date checkinDate, int roomsOccupied, int bedsOccupied) {
        this.id = id;
        this.hotelId = hotelId;
        this.checkinDate = checkinDate;
        this.roomsOccupied = roomsOccupied;
        this.bedsOccupied = bedsOccupied;
    }
}
