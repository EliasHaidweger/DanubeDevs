package db;

import model.Persona;
import model.PersonaHotel;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

/**
 * Data access for personas (user accounts) via Hibernate.
 *
 * Responsible for the following user stories:
 *   Login   - Authentication
 *   US 12   - Manage personas (create, list, edit, delete)
 *   US 13   - Delete permission (canDelete field)
 *   US 24   - Assigning a hotel-persona to a hotel
 */
public class PersonaDAO {

    /** Checks login credentials and returns the corresponding persona (or null). */
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

    /** US 12: Returns all personas, sorted by ID. */
    public List<Persona> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Persona ORDER BY id", Persona.class).list();
        }
    }

    /** Returns a persona using its ID (or null). */
    public Persona findById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Persona.class, id);
        }
    }

    /** US 12: Creates a new persona. The ID is assigned sequentially (incremented by 1). */
    public void save(Persona persona) {
        persona.setId(nextId());
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(persona);
            tx.commit();
        }
    }

    /** US 12/13: Updates an existing persona (e.g., role or deletion permissions). */
    public void update(Persona persona) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(persona);
            tx.commit();
        }
    }

    /** US 12: Deletes a persona along with its hotel assignments. */
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

    /** US 24: Returns the hotel IDs associated with a hotel persona. */
    public List<Integer> findHotelIds(int personaId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "SELECT ph.hotelId FROM PersonaHotel ph WHERE ph.personaId = :personaId",
                            Integer.class)
                    .setParameter("personaId", personaId)
                    .list();
        }
    }

    /** Next available ID (max + 1). This prevents gaps and IDENTITY jumps. */
    private int nextId() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Integer maxId = session.createQuery("SELECT MAX(p.id) FROM Persona p", Integer.class)
                    .uniqueResult();
            return (maxId == null) ? 1 : maxId + 1;
        }
    }
}

