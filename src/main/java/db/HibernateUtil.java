package db;

import model.Hotel;
import model.Occupancy;
import model.Persona;
import model.PersonaHotel;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Zentrale Hibernate-Konfiguration.
 *
 * Die SessionFactory ist das Herzstueck von Hibernate - sie wird EINMAL
 * beim Programmstart erstellt und dann immer wiederverwendet.
 * (Frueher: DatabaseConnection.getConnection() pro Query.)
 *
 * Alle Entity-Klassen werden hier per addAnnotatedClass() registriert.
 */
public class HibernateUtil {

    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                sessionFactory = new Configuration()
                        .configure("hibernate.cfg.xml")   // liest src/main/resources/hibernate.cfg.xml
                        .addAnnotatedClass(Hotel.class)
                        .addAnnotatedClass(Occupancy.class)
                        .addAnnotatedClass(Persona.class)
                        .addAnnotatedClass(PersonaHotel.class)
                        .buildSessionFactory();
            } catch (Exception e) {
                System.err.println("Hibernate konnte nicht initialisiert werden:");
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return sessionFactory;
    }

    /** Beim Programm-Ende aufrufen (optional). */
    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}
