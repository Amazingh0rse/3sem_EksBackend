package facades;


import dto.UserDTO;
import dto.UsersDTO;
import entities.Dog;
import entities.Role;
import entities.User;
import errorhandling.NotFoundException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import security.errorhandling.AuthenticationException;

public class UserFacade {

    private static EntityManagerFactory emf;
    private static UserFacade instance;

    private UserFacade() {
    }

    /**
     *
     * @param _emf
     * @return the instance of this facade.
     */
    public static UserFacade getUserFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new UserFacade();
        }
        return instance;
    }

    public long getUserCount() {
        EntityManager em = emf.createEntityManager();
        try {
            long userCount = (long) em.createQuery("SELECT COUNT(u) FROM User u").getSingleResult();
            return userCount;
        } finally {
            em.close();
        }
    }

    public User getVeryfiedUser(String username, String password) throws AuthenticationException {
        EntityManager em = emf.createEntityManager();
        User user;
        try {
            user = em.find(User.class, username);
            if (user == null || !user.verifyPassword(password)) {
                throw new AuthenticationException("Invalid user name or password");
            }
        } finally {
            em.close();
        }
        return user;
    }

    public UserDTO getUserByName(String userName) throws NotFoundException {
        EntityManager em = emf.createEntityManager();

        User user;
        UserDTO pDTO;

        try {
            user = em.find(User.class, userName);
            if (user == null) {
                throw new NotFoundException("Invalid user name");
            }
        } finally {
            em.close();
        }
        return new UserDTO(user);

    }

    public UsersDTO getAllUsers() throws NotFoundException {

        EntityManager em = emf.createEntityManager();
        UsersDTO usDTO;
        try {
            usDTO = new UsersDTO(em.createQuery("SELECT p FROM User p").getResultList());
        } catch (Exception e) {
            throw new NotFoundException("No connection to the database");
        } finally {
            em.close();
        }
        return usDTO;

    }
        
    public UserDTO deleteUser(String userName) throws NotFoundException {
        EntityManager em = emf.createEntityManager();
        User user = em.find(User.class, userName);
        if (user == null) {
            throw new NotFoundException("Could not delete, provided id does not exist");
        } else {
            try {
                em.getTransaction().begin();
                em.remove(user);
                em.getTransaction().commit();
            } finally {
                em.close();
            }
            return new UserDTO(user);
        }
    }
    
    public void addDogToUser(String userName, long id) throws NotFoundException {
        EntityManager em = emf.createEntityManager();

        User user = em.find(User.class, userName);
        Dog dog = em.find(Dog.class, id);

        user.addDog(dog);

        if (user == null) {
            throw new NotFoundException("No user found");
        }
        try {
            em.getTransaction().begin();
            em.merge(user);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public void removeDogFromUser(String userName, long id) throws NotFoundException {
        EntityManager em = emf.createEntityManager();

        User user = em.find(User.class, userName);
        Dog dog = em.find(Dog.class, id);

        user.removeDog(dog);

        if (user == null) {
            throw new NotFoundException("No person found");
        }
        try {
            em.getTransaction().begin();
            em.merge(user);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}


