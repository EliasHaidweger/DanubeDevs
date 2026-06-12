package db;

import model.Occupancy;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

/**
 * Datenzugriff fuer Belegungen (Occupancies) ueber Hibernate.
 *
 * Zustaendig fuer die Persistenz folgender User Stories:
 *   US 2  - Belegung aller Hotels fuer einen Monat
 *   US 6  - Belegung erfassen
 *   US 10 - Belegungen eines Hotels in einem Zeitraum
 *   US 27 - Alle eigenen Belegungen eines Hotels
 */
public class OccupancyDAO {

    /** US 2: Belegungen aller Hotels fuer einen bestimmten Monat/Jahr. */
    public List<Occupancy> findByMonth(int year, int month) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Occupancy o WHERE o.year = :year AND o.month = :month",
                    Occupancy.class)
                    .setParameter("year", year)
                    .setParameter("month", month)
                    .list();
        }
    }

    /** US 27: Alle Belegungen eines Hotels (neueste zuerst). */
    public List<Occupancy> findByHotel(int hotelId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Occupancy o WHERE o.hotelId = :hotelId ORDER BY o.year DESC, o.month DESC",
                    Occupancy.class)
                    .setParameter("hotelId", hotelId)
                    .list();
        }
    }

    /**
     * US 10: Belegungen eines Hotels in einem Zeitraum (von/bis Monat+Jahr).
     * Der Ausdruck (year * 12 + month) bildet jeden Monat auf eine
     * fortlaufende Zahl ab und macht den Bereichsvergleich einfach.
     */
    public List<Occupancy> findByHotelInRange(int hotelId,
                                              int fromYear, int fromMonth,
                                              int toYear, int toMonth) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Occupancy o WHERE o.hotelId = :hotelId "
                  + "AND (o.year * 12 + o.month) >= :from "
                  + "AND (o.year * 12 + o.month) <= :to "
                  + "ORDER BY o.year, o.month",
                    Occupancy.class)
                    .setParameter("hotelId", hotelId)
                    .setParameter("from", fromYear * 12 + fromMonth)
                    .setParameter("to", toYear * 12 + toMonth)
                    .list();
        }
    }

    /**
     * US 6: Speichert eine Belegung.
     * merge() fuegt neu ein oder aktualisiert, falls fuer diesen
     * Schluessel (Hotel + Jahr + Monat) bereits ein Eintrag existiert.
     */
    public void save(Occupancy occupancy) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(occupancy);
            tx.commit();
        }
    }
}
