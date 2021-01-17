package utils;

import entities.Dog;
import entities.Role;
import entities.User;
import java.time.LocalDate;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class SetupTestDogs {

    public static void main(String[] args) {

        EntityManagerFactory emf = EMF_Creator.createEntityManagerFactory();
        EntityManager em = emf.createEntityManager();

        Role userRole = new Role("user");
        Role adminRole = new Role("admin");

        User u1 = new User("John Dillermand", "supersecretpassword");
        User u2 = new User("Niels Buckingham", "supersecretpassword");
        User u3 = new User("Robert Dølhus", "supersecretpassword");
        User u4 = new User("Kenny Nickelman", "supersecretpassword");
        User u5 = new User("Jan Monrad", "HundogHund");

        Dog d1 = new Dog("Pølse", LocalDate.of(1989, 12, 18), "This is my Wiener", "mexicanhairless");
        Dog d2 = new Dog("Løg", LocalDate.of(2010, 5, 19), "Min helt egen chow!", "chow");
        Dog d3 = new Dog("Sigurd", LocalDate.of(2016, 2, 3), "Sigurd Elsker bjørnetime!", "husky");
        Dog d4 = new Dog("Bjørn", LocalDate.of(2015, 4, 8), "STOR BAMSE!!", "chow");
        Dog d5 = new Dog("Mollie", LocalDate.of(2011, 2, 3), "Det her er min mollie", "beagle");
        Dog d6 = new Dog("Broxigar", LocalDate.of(1953, 2, 13), "Broxigar er lidt mærkelig.. ", "samoyed");
        Dog d7 = new Dog("Pepsi", LocalDate.of(2020, 1, 1), "This is my pitbull", "pitbull");
        Dog d8 = new Dog("MoonMoon", LocalDate.of(2016, 2, 27), "God damnit MoonMoon", "samoyed");
        Dog d9 = new Dog("Darth Vader", LocalDate.of(2013, 5, 24), "I think he might have faceplanted the wall", "pug");
        

        if (u1.getUserPassword().equals("test") || u2.getUserPassword().equals("test") || u3.getUserPassword().equals("test")) {
            throw new UnsupportedOperationException("You have not changed the passwords");
        }

        em.getTransaction().begin();

        //em.createQuery("delete from Dog").executeUpdate();
        em.createQuery("delete from User").executeUpdate();
        em.createQuery("delete from Role").executeUpdate();

        u1.addRole(userRole);
        u2.addRole(userRole);
        u3.addRole(userRole);
        u4.addRole(userRole);
        u5.addRole(userRole);
        u5.addRole(adminRole);

        u1.addDog(d1);
        u1.addDog(d2);
        u2.addDog(d3);
        u2.addDog(d4);
        u3.addDog(d5);
        u3.addDog(d6);
        u4.addDog(d7);
        u4.addDog(d8);
        u5.addDog(d9);
        
        em.persist(u1);
        em.persist(u2);
        em.persist(u3);
        em.persist(u4);
        em.persist(u5);
        em.persist(userRole);
        em.persist(adminRole);
        
        
        em.getTransaction().commit();
        System.out.println("PW: " + u1.getUserPassword());
        System.out.println("Testing user with OK password: " + u1.verifyPassword("supersecretpassword"));
        System.out.println("Testing user with wrong password: " + u1.verifyPassword("test1"));
        System.out.println("Created TEST Users");
        
        //Removes relations from database
//        em.getTransaction().begin();
//        p3.removeHobby(h2);
//        em.getTransaction().commit();
    }
}