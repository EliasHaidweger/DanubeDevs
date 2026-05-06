import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class PersonCrudShowcase {


    private static final SessionFactory sf = HibernateUtil.getSessionFactory();

    public static void main(String[] args) {

        line("CREATE");
        Person p = create("Michael", "Deutsch", "michael@test.com", 111);
        line(p.toString());


        line("GET BY ID");

      Person temp =  getPersonById(p.getId());
        System.out.println(temp);

        line("GET ALL");
        getAll();
/*
        line("UPDATE");
        update(p.getId(), "Michael Updated", 36);

        line("DELETE");
        delete(p.getId());

        line("GET ALL AFTER DELETE");
        getAll();


       */
        sf.close();
    }

    static Person create(String v, String n, String e, int a) {
        Person p = Person.builder().vorname(v).nachname(n).email(e).age(a).build();

        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();

            s.persist(p);

            tx.commit();
        }
        System.out.println(p);
        return p;
    }

    static Person getPersonById(Long id) {
        try (Session s = sf.openSession()) {
            System.out.println();
            return s.get(Person.class, id);
        }
    }

    static void getAll() {
        try (Session s = sf.openSession()) {
            List<Person> list = s.createQuery("from Person", Person.class).list();
            list.forEach(System.out::println);
        }
    }

    static void update(Long id, String name, int alter) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            Person p = s.get(Person.class, id);
            if (p != null) {
                p.setVorname(name);
                p.setAge(alter);
            }
            tx.commit();
            System.out.println(p);
        }
    }

    static void delete(Long id) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            Person p = s.get(Person.class, id);
            if (p != null) s.remove(p);
            tx.commit();
            System.out.println("Deleted: " + p);
        }
    }

    static void line(String t) {
        System.out.println("\n==== " + t + " ===================================\n");
    }
}