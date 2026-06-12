package db;

import model.Persona;
import model.PersonaHotel;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

/**
 * Datenzugriff fuer Personas (Benutzerkonten) ueber Hibernate.
 *
 * Zustaendig fuer:
 *   Login   - Authentifizierung
 *   US 12   - Personas verwalten (anlegen, auflisten, bearbeiten, loeschen)
 *   US 13   - Loeschberechtigung (Feld canDelete)
 *   US 24   - Hotelzuordnung einer Hotel-Persona
 */
public class PersonaDAO {

    /** Prueft Login-Daten und liefert die passende Persona (oder null). */
    public Persona login(String username, String password) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Persona p WHERE p.username = :username AND p.password = :password",
                    Persona.class)
                    .setParameter("username", username)
                    .setParameter("password", password)
                    .uniqueResult();
        }
    }

    /** US 12: Liefert alle Personas, sortiert nach ID. */
    public List<Persona> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Persona ORDER BY id", Persona.class).list();
        }
    }

    /** Liefert eine Persona anhand ihrer ID (oder null). */
    public Persona findById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Persona.class, id);
        }
    }

    /** US 12: Legt eine neue Persona an. Die ID wird fortlaufend vergeben (max + 1). */
    public void save(Persona persona) {
        persona.setId(nextId());
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(persona);
            tx.commit();
        }
    }

    /** US 12/13: Aktualisiert eine bestehende Persona (z.B. Rolle oder Loeschrecht). */
    public void update(Persona persona) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(persona);
            tx.commit();
        }
    }

    /** US 12: Loescht eine Persona samt ihrer Hotelzuordnungen. */
    public void delete(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            session.createMutationQuery("DELETE FROM PersonaHotel ph WHERE ph.personaId = :id")
                   .setParameter("id", id).executeUpdate();

            Persona persona = session.get(Persona.class, id);
            if (persona != null) session.remove(persona);

            tx.commit();
        }
    }

    /** US 24: Liefert die Hotel-IDs, die einer Hotel-Persona zugeordnet sind. */
    public List<Integer> findHotelIds(int personaId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "SELECT ph.hotelId FROM PersonaHotel ph WHERE ph.personaId = :personaId",
                    Integer.class)
                    .setParameter("personaId", personaId)
                    .list();
        }
    }

    /** Naechste freie ID (max + 1) - vermeidet Luecken und IDENTITY-Spruenge. */
    private int nextId() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Integer maxId = session.createQuery("SELECT MAX(p.id) FROM Persona p", Integer.class)
                                   .uniqueResult();
            return (maxId == null) ? 1 : maxId + 1;
        }
    }
}
