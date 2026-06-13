package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Maintains the logged-in status for the duration of the program:
 * the currently logged-in user and the IDs of the hotels associated for a hotel user.
 *
 * This is deliberately NOT a Hibernate entity, but purely an application state.
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
