package db;

import model.Hotel;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    private static SessionFactory factory;

    public static SessionFactory getSessionFactory() {
        if (factory == null) {
            factory = new Configuration()
                    .configure()              // liest hibernate.cfg.xml
                    .addAnnotatedClass(Hotel.class)
                    .buildSessionFactory();
        }
        return factory;
    }
}