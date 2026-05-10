package model;

import lombok.Data;

/**
 * Repraesentiert eine Zeile aus dbo.hotels.
 * Lombok @Data generiert automatisch Getter, Setter, equals, hashCode.
 */
@Data
public class Hotel {

    private int id;
    private String category;
    private String name;
    private String owner;
    private String contact;
    private String address;
    private String city;
    private String cityCode;
    private String phone;
    private int noRooms;
    private int noBeds;
    private String tags;

    @Override
    public String toString() {
        return id + " - " + name;
    }
}
