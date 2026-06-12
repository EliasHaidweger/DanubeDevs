package model;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

/**
 * Hibernate entity for the persona_hotels table (n:m relationship).
 * Which persona is allowed to view/edit which hotels?
 *
 * Composite key (persona_id + hotel_id).
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

    /** Composite key class. */
    @Data
    public static class PersonaHotelId implements Serializable {
        private int personaId;
        private int hotelId;
    }
}
