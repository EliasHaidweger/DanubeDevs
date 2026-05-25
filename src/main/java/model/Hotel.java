package model;

import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
@Table(name = "hotels")
public class Hotel {

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "category")
    private String category;

    @Column(name = "name")
    private String name;

    @Column(name = "owner")
    private String owner;

    @Column(name = "contact")
    private String contact;

    @Column(name = "address")
    private String address;

    @Column(name = "city")
    private String city;

    @Column(name = "cityCode")
    private String cityCode;

    @Column(name = "phone")
    private String phone;

    @Column(name = "noRooms")
    private int noRooms;

    @Column(name = "noBeds")
    private int noBeds;

    @Column(name = "tags")
    private String tags;

    @Override
    public String toString() {
        return id + " - " + name;
    }
}


/*
package model;


import lombok.Data;


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
*/