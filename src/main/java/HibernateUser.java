import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUser {
    static {
        System.setProperty("org.jboss.logging.provider", "slf4j");
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "off");
        System.setProperty("org.slf4j.simpleLogger.log.org.hibernate", "off");
        System.setProperty("org.slf4j.simpleLogger.log.org.jboss", "off");
    }

    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {

        try {
            return new Configuration()
                    .configure()               // load the config file
                    .addAnnotatedClass(Users.class)  // register entity, add more if needed
                    .buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("SessionFactory couldnt be created.");
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
