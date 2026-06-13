package db;

import model.Hotel;
import model.Occupancy;
import model.Persona;
import model.PersonaHotel;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Provides the central Hibernate SessionFactory.
 *
 * The SessionFactory is a heavyweight object and is therefore created only ONCE
 * and reused throughout the entire program.
 * The DAOs use it to open short-lived sessions as needed.
 *
 * The configuration (DB connection) is specified in hibernate.cfg.xml; the entity classes are registered here.
 */
public class HibernateUtil {

    private static final SessionFactory SESSION_FACTORY = buildSessionFactory();

    private HibernateUtil() {
        // Utility-Klasse - keine Instanzen
    }

    private static SessionFactory buildSessionFactory() {
        try {
            return new Configuration()
                    .configure("hibernate.cfg.xml")
                    .addAnnotatedClass(Hotel.class)
                    .addAnnotatedClass(Occupancy.class)
                    .addAnnotatedClass(Persona.class)
                    .addAnnotatedClass(PersonaHotel.class)
                    .buildSessionFactory();
        } catch (RuntimeException e) {
            throw new IllegalStateException(
                    "Hibernate konnte nicht initialisiert werden. "
                            + "Bitte hibernate.cfg.xml (DB-Verbindung) pruefen.", e);
        }
    }

    public static SessionFactory getSessionFactory() {
        return SESSION_FACTORY;
    }

    /** Closes the SessionFactory (at the end of the program). */
    public static void shutdown() {
        SESSION_FACTORY.close();
    }
}

