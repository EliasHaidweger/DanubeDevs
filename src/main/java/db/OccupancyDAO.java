package db;

import model.Occupancy;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

/**
 * Data access for occupancy records via Hibernate.
 *
 * Responsible for the following user stories:
 *   US 2  - Monthly occupancy rates for all hotels
 *   US 6  - Record occupancy
 *   US 10 - Hotel occupancy rates for a specific period
 *   US 27 - All of a hotel's own bookings
 */
public class OccupancyDAO {

    /** US 2: Room availability at all hotels for a specific month/year. */
    public List<Occupancy> findByMonth(int year, int month) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Occupancy o WHERE o.year = :year AND o.month = :month", Occupancy.class).setParameter("year", year).setParameter("month", month).list();
        }
    }

    /** US 27: All hotel bookings (most recent first). */
    public List<Occupancy> findByHotel(int hotelId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Occupancy o WHERE o.hotelId = :hotelId ORDER BY o.year DESC, o.month DESC",
                    Occupancy.class)
                    .setParameter("hotelId", hotelId)
                    .list();
        }
    }

    /** US 10: Hotel occupancy rates for a specific period (from/to month+year). */
    public List<Occupancy> findByHotelInRange(int hotelId, int fromYear, int fromMonth, int toYear, int toMonth) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Occupancy o WHERE o.hotelId = :hotelId " + "AND (o.year * 12 + o.month) >= :from "
                  + "AND (o.year * 12 + o.month) <= :to " + "ORDER BY o.year, o.month", Occupancy.class)
                    .setParameter("hotelId", hotelId)
                    .setParameter("from", fromYear * 12 + fromMonth)
                    .setParameter("to", toYear * 12 + toMonth)
                    .list();
        }
    }

    /**
     * US 6: Saves a reservation.
     * merge() adds a new entry or updates the existing one if an entry already exists for this key (Hotel + Year + Month).
     */
    public void save(Occupancy occupancy) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(occupancy);
            tx.commit();
        }
    }
}
