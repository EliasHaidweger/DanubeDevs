package model;

import lombok.Data;

import java.io.Serializable;

/**
 * Zusammengesetzter Primaerschluessel fuer Occupancy.
 * Die occupancies-Tabelle hat PRIMARY KEY (hotel_id, year, month),
 * also brauchen wir eine eigene Klasse die diese 3 Felder buendelt.
 *
 * Muss Serializable sein und equals/hashCode haben (macht Lombok @Data).
 * Die Feldnamen muessen exakt zu den @Id-Feldern in Occupancy passen.
 */
@Data
public class OccupancyId implements Serializable {

    private int hotelId;
    private int year;
    private int month;
}
