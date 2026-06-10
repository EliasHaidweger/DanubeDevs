package db;

import model.Persona;
import model.PersonaHotel;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Persona-Datenzugriff ueber Hibernate.
 */
public class PersonaDAO {

    /** Login pruefen. */
    public Persona login(String username, String password) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Persona p WHERE p.username = :u AND p.password = :pw",
                    Persona.class)
                    .setParameter("u", username)
                    .setParameter("pw", password)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Alle Personas (Story #12). */
    public List<Persona> getAllPersonas() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Persona ORDER BY id", Persona.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    /** Naechste freie ID (max + 1). */
    public int getNextId() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Integer maxId = session.createQuery("SELECT MAX(p.id) FROM Persona p", Integer.class)
                                   .uniqueResult();
            return (maxId == null) ? 1 : maxId + 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    /** Persona hinzufuegen (Story #12). ID = max + 1. */
    public boolean insertPersona(Persona p) {
        p.setId(getNextId());

        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(p);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    /** Persona aktualisieren (Story #13). */
    public boolean updatePersona(Persona p) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(p);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    /** Persona loeschen (inkl. ihrer Hotel-Verknuepfungen). */
    public boolean deletePersona(int id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            // Erst die Verknuepfungen in persona_hotels
            session.createMutationQuery("DELETE FROM PersonaHotel ph WHERE ph.personaId = :id")
                   .setParameter("id", id).executeUpdate();

            // Dann die Persona selbst
            Persona p = session.get(Persona.class, id);
            if (p != null) session.remove(p);

            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    /** Welche Hotel-IDs gehoeren einer Hotel-Rep-Persona? */
    public List<Integer> getHotelIdsForPersona(int personaId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "SELECT ph.hotelId FROM PersonaHotel ph WHERE ph.personaId = :id",
                    Integer.class)
                    .setParameter("id", personaId)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
