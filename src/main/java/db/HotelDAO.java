package db;

import model.Hotel;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

/**
 * Data access for hotels using Hibernate.
 *
 * Responsible for the following user stories:
 *   US 3  - Add a hotel
 *   US 4  - List of all hotels
 *   US 5  - Edit Hotel
 *   US 11 - Delete hotel
 */
public class HotelDAO {

    /** US 4: Returns all hotels, sorted by ID. */
    public List<Hotel> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Hotel ORDER BY id", Hotel.class).list();
        }
    }

    /** Returns a hotel based on its ID (or null if none exists). */
    public Hotel findById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Hotel.class, id);
        }
    }

    /** US 3: Create a new hotel. The ID is assigned sequentially (incremented by 1). */
    public void save(Hotel hotel) {
        hotel.setId(nextId());
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(hotel);
            tx.commit();
        }
    }

    /** US 5: Updates an existing hotel. */
    public void update(Hotel hotel) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(hotel);
            tx.commit();
        }
    }

    /** US 11: Delete a hotel along with its reservations */
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

    /** Next available ID (max + 1). This prevents gaps and IDENTITY jumps. */
    private int nextId() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Integer maxId = session.createQuery("SELECT MAX(h.id) FROM Hotel h", Integer.class)
                    .uniqueResult();
            return (maxId == null) ? 1 : maxId + 1;
        }
    }
}

