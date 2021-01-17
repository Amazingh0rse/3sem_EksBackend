package facades;


import dto.PersonDTO;
import dto.PersonsDTO;
import dto.UserDTO;
import dto.UsersDTO;
import entities.Address;
import entities.Hobby;
import entities.Person;
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
            usDTO = new UsersDTO(em.createQuery("SELECT p FROM Users p").getResultList());
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
}


//    public PersonDTO makePerson(PersonDTO person) throws AuthenticationException {
//
//        String email = person.getEmail();
//        String userPass = person.getPassword();
//        String phone = person.getPhone();
//        String fName = person.getFirstName();
//        String lName = person.getLastName();
//
//        Person newPerson;
//
//        EntityManager em = emf.createEntityManager();
//
//        try {
//            newPerson = em.find(Person.class, email);
//            if (newPerson == null && email.length() > 0 && userPass.length() > 0) {
//                newPerson = new Person(email, userPass, phone, fName, lName);
//                Role userRole = em.find(Role.class, "user");
//                newPerson.addRole(userRole);
//
//                TypedQuery<Address> addressList = em.createQuery("SELECT a FROM Address a", Address.class);
//                List<Address> resultList = addressList.getResultList();
//
//                boolean flag = true;
//
//                for (int i = 0; i < resultList.size(); i++) {
//                    if (person.getStreet().equalsIgnoreCase(resultList.get(i).getStreet())
//                            && person.getCity().equalsIgnoreCase(resultList.get(i).getCity())
//                            && person.getZipcode() == resultList.get(i).getZipCode()) {
//                        newPerson.setAddress(resultList.get(i));
//                        flag = false;
//                    }
//                }
//                if (flag) {
//                    Address newAddress = new Address(person.getStreet(), person.getCity(), person.getZipcode());
//                    newPerson.setAddress(newAddress);
//                }
//
//                Hobby hobby = new Hobby("None", "none");
//
//                newPerson.addHobby(hobby);
//
//                em.getTransaction().begin();
//                em.persist(newPerson);
//                em.getTransaction().commit();
//            } else {
//                if ((email.length() == 0 || userPass.length() == 0)) {
//                    throw new AuthenticationException("Missing input");
//                }
//                if (newPerson.getEmail().equalsIgnoreCase(person.getEmail())) {
//                    throw new AuthenticationException("User exist");
//                }
//            }
//        } finally {
//            em.close();
//        }
//        return new PersonDTO(newPerson);
//    }

