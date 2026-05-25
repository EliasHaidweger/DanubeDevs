package db;

import model.Hotel;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;


public class HotelDAOHibernate {

    /** Alle Hotels lesen. */
    public List<Hotel> getAllHotels() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Hotel order by id", Hotel.class).list();
        }
    }

    /** Neues Hotel anlegen. */
    public boolean insertHotel(Hotel h) {
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

    /** Hotel aktualisieren. */
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

    /** Hotel loeschen. */
    public boolean deleteHotel(int id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
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

    /** Naechste freie ID (max + 1). */
    public int getNextId() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Integer max = session.createQuery("select max(id) from Hotel", Integer.class)
                    .uniqueResult();
            return (max == null) ? 1 : max + 1;
        }
    }
}
