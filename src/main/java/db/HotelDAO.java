package db;

import model.Hotel;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

/**
 * Datenzugriff fuer Hotels ueber Hibernate.
 *
 * Zustaendig fuer die Persistenz folgender User Stories:
 *   US 3  - Hotel anlegen
 *   US 4  - Liste aller Hotels
 *   US 5  - Hotel bearbeiten
 *   US 11 - Hotel loeschen
 */
public class HotelDAO {

    /** US 4: Liefert alle Hotels, sortiert nach ID. */
    public List<Hotel> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Hotel ORDER BY id", Hotel.class).list();
        }
    }

    /** Liefert ein Hotel anhand seiner ID (oder null, wenn keines existiert). */
    public Hotel findById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Hotel.class, id);
        }
    }

    /** US 3: Legt ein neues Hotel an. Die ID wird fortlaufend vergeben (max + 1). */
    public void save(Hotel hotel) {
        hotel.setId(nextId());
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(hotel);
            tx.commit();
        }
    }

    /** US 5: Aktualisiert ein bestehendes Hotel. */
    public void update(Hotel hotel) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(hotel);
            tx.commit();
        }
    }

    /** US 11: Loescht ein Hotel samt seiner Belegungen und Zuordnungen. */
    public void delete(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            session.createMutationQuery("DELETE FROM Occupancy o WHERE o.hotelId = :id")
                   .setParameter("id", id).executeUpdate();
            session.createMutationQuery("DELETE FROM PersonaHotel ph WHERE ph.hotelId = :id")
                   .setParameter("id", id).executeUpdate();

            Hotel hotel = session.get(Hotel.class, id);
            if (hotel != null) session.remove(hotel);

            tx.commit();
        }
    }

    /** Naechste freie ID (max + 1) - vermeidet Luecken und IDENTITY-Spruenge. */
    private int nextId() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Integer maxId = session.createQuery("SELECT MAX(h.id) FROM Hotel h", Integer.class)
                                   .uniqueResult();
            return (maxId == null) ? 1 : maxId + 1;
        }
    }
}
