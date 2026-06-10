package db;

import model.Occupancy;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

/**
 * Occupancy-Datenzugriff ueber Hibernate.
 */
public class OccupancyDAO {

    /** Alle Belegungen eines Hotels (Story #10). */
    public List<Occupancy> getOccupanciesByHotel(int hotelId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Occupancy o WHERE o.hotelId = :hid ORDER BY o.year DESC, o.month DESC",
                    Occupancy.class)
                    .setParameter("hid", hotelId)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    /** Belegungen fuer einen bestimmten Monat/Jahr (Story #2). */
    public List<Occupancy> getOccupanciesByMonth(int year, int month) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Occupancy o WHERE o.year = :y AND o.month = :m",
                    Occupancy.class)
                    .setParameter("y", year)
                    .setParameter("m", month)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    /**
     * Belegungen eines Hotels in einem Zeitraum (Story #10 mit FROM/TO).
     * Trick: (year*12 + month) ergibt eine fortlaufende Zahl zum Vergleichen.
     */
    public List<Occupancy> getOccupanciesByHotelInRange(int hotelId,
                                                        int fromYear, int fromMonth,
                                                        int toYear, int toMonth) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Occupancy o WHERE o.hotelId = :hid "
                  + "AND (o.year * 12 + o.month) >= :from "
                  + "AND (o.year * 12 + o.month) <= :to "
                  + "ORDER BY o.year, o.month",
                    Occupancy.class)
                    .setParameter("hid", hotelId)
                    .setParameter("from", fromYear * 12 + fromMonth)
                    .setParameter("to", toYear * 12 + toMonth)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    /**
     * Speichern (INSERT oder UPDATE - Story #6).
     * Hibernate's merge() macht automatisch INSERT wenn neu, UPDATE wenn vorhanden.
     */
    public boolean saveOccupancy(Occupancy o) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(o);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }
}
