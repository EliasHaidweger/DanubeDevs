package model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Hibernate-Entity fuer die hotels-Tabelle.
 *
 * @Entity      = das ist eine Hibernate-Entitaet
 * @Table       = welche DB-Tabelle dahinter steht
 * @Id          = Primaerschluessel
 * @Column      = Spaltenname falls anders als Feldname
 *
 * Die ID wird NICHT von der DB generiert (kein @GeneratedValue),
 * sondern manuell vergeben (max+1) - so vermeiden wir IDENTITY-Spruenge.
 */
@Entity
@Table(name = "hotels")
@Data
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
