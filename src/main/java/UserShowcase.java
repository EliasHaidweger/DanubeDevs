import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class UserShowcase {
    private static final SessionFactory sf = HibernateUtil.getSessionFactory();

    public static void main(String[] args) {

        line("CREATE");
        //Users p = create("Martin", 45, Benutzerrolle.SENIOR);
        //line(p.toString());

        Users m = create("Max", 34);
        line(m.toString());

/*
        line("GET BY ID");

      Person temp =  getPersonById(p.getId());
        System.out.println(temp);

        line("GET ALL");
        getAll();

        line("UPDATE");
        update(p.getId(), "Michael Updated", 36);

        line("DELETE");
        delete(p.getId());

        line("GET ALL AFTER DELETE");
        getAll();


       */
        sf.close();
    }

    static Users create(String v, int  n ) {
        //Users p = Users.builder().name(v).age(n).role(e).build();
        Users p = Users.builder().name(v).age(n).build();
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();

            s.persist(p);

            tx.commit();
        }
        System.out.println(p);
        return p;
    }
    /*
        static Hotelnew getPersonById(Long id) {
            try (Session s = sf.openSession()) {
                System.out.println();
                return s.get(Hotelnew.class, id);
            }
        }

        static void getAll() {
            try (Session s = sf.openSession()) {
                List<Hotelnew> list = s.createQuery("from Person", Hotelnew.class).list();
                list.forEach(System.out::println);
            }
        }

        static void update(String name, String city) {
            try (Session s = sf.openSession()) {
                Transaction tx = s.beginTransaction();
                Hotelnew p = s.get(Hotelnew.class, id);
                if (p != null) {
                    p.setName(name);
                    p.setCity(city);
                }
                tx.commit();
                System.out.println(p);
            }
        }

        static void delete(Long id) {
            try (Session s = sf.openSession()) {
                Transaction tx = s.beginTransaction();
                Hotelnew p = s.get(Hotelnew.class, id);
                if (p != null) s.remove(p);
                tx.commit();
                System.out.println("Deleted: " + p);
            }
        }
    */
    static void line(String t) {
        System.out.println("\n==== " + t + " ===================================\n");
    }
}
