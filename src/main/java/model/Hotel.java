package model;

public class Hotel {
    public int id;
    public String category;
    public String name;
    public String city;
    public Integer noRooms;
    public Integer noBeds;

    public Hotel(int id, String category, String name, String city, Integer noRooms, Integer noBeds) {
        this.id = id;
        this.category = category;
        this.name = name;
        this.city = city;
        this.noRooms = noRooms;
        this.noBeds = noBeds;
    }
}
