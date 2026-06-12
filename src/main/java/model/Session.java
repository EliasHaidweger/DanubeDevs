package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Haelt den Anmeldezustand waehrend der Programmlaufzeit fest:
 * die aktuell angemeldete Persona und - bei einem Hotel-Benutzer -
 * die IDs seiner zugeordneten Hotels.
 *
 * Bewusst KEINE Hibernate-Entity, sondern reiner Anwendungszustand.
 */
public class Session {

    private static Persona currentPersona;
    private static List<Integer> myHotelIds = new ArrayList<>();

    public static Persona getCurrentPersona() {
        return currentPersona;
    }

    public static void setCurrentPersona(Persona persona) {
        currentPersona = persona;
    }

    public static List<Integer> getMyHotelIds() {
        return myHotelIds;
    }

    public static void setMyHotelIds(List<Integer> ids) {
        myHotelIds = ids;
    }

    public static void clear() {
        currentPersona = null;
        myHotelIds = new ArrayList<>();
    }
}
