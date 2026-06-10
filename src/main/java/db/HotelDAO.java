package db;

import model.Hotel;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

/**
 * Hotel-Datenzugriff ueber Hibernate.
 *
 * Statt SQL-Strings nutzen wir jetzt:
 *   - session.get()       zum Laden per ID
 *   - session.createQuery (HQL) fuer Listen
 *   - session.persist()   zum Einfuegen
 *   - session.merge()     zum Aktualisieren
 *   - session.remove()    zum Loeschen
 *
 * HQL (Hibernate Query Language) arbeitet mit Klassennamen statt Tabellennamen,
 * z.B. "FROM Hotel" statt "SELECT * FROM hotels".
 */
public class HotelDAO {

    /** Alle Hotels (Story #4). */
    public List<Hotel> getAllHotels() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Hotel ORDER BY id", Hotel.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    /** Ein Hotel anhand der ID. */
    public Hotel getHotelById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Hotel.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Liefert die naechste freie ID (max + 1).
     * Wenn die Tabelle leer ist -> 1.
     */
    public int getNextId() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Integer maxId = session.createQuery("SELECT MAX(h.id) FROM Hotel h", Integer.class)
                                   .uniqueResult();
            return (maxId == null) ? 1 : maxId + 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    /**
     * Neues Hotel anlegen (Story #3).
     * ID wird selbst berechnet (max + 1).
     */
    public boolean insertHotel(Hotel h) {
        h.setId(getNextId());

        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(h);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    /** Hotel aktualisieren (Story #5). */
    public boolean updateHotel(Hotel h) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(h);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    /** Hotel + alle abhaengigen Daten loeschen (Story #11). */
    public boolean deleteHotel(int id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            // Erst abhaengige Daten loeschen (occupancies, bookings, persona_hotels)
            session.createMutationQuery("DELETE FROM Occupancy o WHERE o.hotelId = :id")
                   .setParameter("id", id).executeUpdate();
            session.createMutationQuery("DELETE FROM PersonaHotel ph WHERE ph.hotelId = :id")
                   .setParameter("id", id).executeUpdate();

            // bookings hat keine Entity -> per native SQL
            session.createNativeMutationQuery("DELETE FROM bookings WHERE hotel_id = :id")
                   .setParameter("id", id).executeUpdate();

            // Jetzt das Hotel selbst
            Hotel h = session.get(Hotel.class, id);
            if (h != null) session.remove(h);

            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }
}
