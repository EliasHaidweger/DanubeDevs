package model;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

/**
 * Hibernate-Entity fuer die persona_hotels-Tabelle (n:m Verknuepfung).
 * Welche Persona darf welche Hotels sehen/bearbeiten.
 *
 * Zusammengesetzter Schluessel (persona_id + hotel_id).
 */
@Entity
@Table(name = "persona_hotels")
@IdClass(PersonaHotel.PersonaHotelId.class)
@Data
public class PersonaHotel {

    @Id
    @Column(name = "persona_id")
    private int personaId;

    @Id
    @Column(name = "hotel_id")
    private int hotelId;

    /** Composite-Key-Klasse. */
    @Data
    public static class PersonaHotelId implements Serializable {
        private int personaId;
        private int hotelId;
    }
}
