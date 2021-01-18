package facades;


import dto.UserDTO;
import dto.UsersDTO;
import entities.Dog;
import entities.Role;
import entities.User;
import utils.EMF_Creator;

import errorhandling.NotFoundException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import static org.hamcrest.MatcherAssert.assertThat;
import org.hamcrest.Matchers;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import security.errorhandling.AuthenticationException;

//Uncomment the line below, to temporarily disable this test
@Disabled
public class UserFacadeTest {

    private static EntityManagerFactory emf;
    //private static FacadeExample facade;
    private static UserFacade facade;
    private User u1, u2, u3;
    private Role r1, r2;
    private Dog d1, d2, d3, d4;

    public UserFacadeTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        facade = UserFacade.getUserFacade(emf);
    }

    @AfterAll
    public static void tearDownClass() {
//        Clean up database after test is done or use a persistence unit with drop-and-create to start up clean on every test
    }

    // Setup the DataBase in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the code below to use YOUR OWN entity class
    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        try {
//Delete existing users and roles to get a "fresh" database
            em.getTransaction().begin();
            //em.createQuery("delete from User").executeUpdate();
            //em.createQuery("delete from Role").executeUpdate();
            //em.createQuery("delete from Dog").executeUpdate();

            u1 = new User("John Dillermand", "supersecretpassword");
            u2 = new User("Niels Buckingham", "supersecretpassword");
            u3 = new User("Robert Dølhus", "supersecretpassword");

            d1 = new Dog("Pølse", "1989/12/18", "This is my Wiener", "mexicanhairless");
            d2 = new Dog("Løg", "2010/5/19", "Min helt egen chow!", "chow");
            d3 = new Dog("Sigurd", "2016/2/3", "Sigurd Elsker bjørnetime!", "husky");
            d4 = new Dog("Troubadour", "2016/2/3", "Sigurd Elsker bjørnetime!", "husky");
            
            r1 = new Role("user");
            r2 = new Role("admin");

            u1.addDog(d1);
            u2.addDog(d2);
            u2.addDog(d3);
  
            u1.addRole(r1);
            u2.addRole(r2);
           
            em.persist(u1);
            em.persist(u2);
            em.persist(u3);
            em.persist(r1);
            em.persist(r2);
            
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @AfterEach
    public void tearDown() {
//        Remove any data after each test was run
    }

    @Test
    public void testUserCount() {
        assertEquals(3, facade.getUserCount(), "Expects three rows in the database");
    }

    @Test
    public void testGetVeryfiedUser() throws AuthenticationException {
        String pass = u1.getUserPassword();

        assertEquals(u1.getUserName(), "John Dillermand");
        assertEquals(u1.getUserPassword(), pass);
        assertThat(u1.getUserName(), is(not("pollemand")));
        assertThat(u1.getUserPassword(), is(not("lilleGrimTomat")));
    }

    @Test
    public void testGetRoleList() {
        assertEquals(u1.getRolesAsStrings().get(0), r1.getRoleName());
    }

    

    @Test
    public void testGetAllUsers() throws NotFoundException {

        UsersDTO usersDTO = facade.getAllUsers();
        List<UserDTO> list = usersDTO.getAll();
        System.out.println("Liste af personer: " + list);
        assertThat(list, everyItem(Matchers.hasProperty("userName")));
        assertThat(list, Matchers.hasItems(Matchers.<UserDTO>hasProperty("userName", is("John Dillermand")),
                Matchers.<UserDTO>hasProperty("userName", is("Niels Buckingham"))
        ));

    }

    @Test
    public void getUserByName() throws NotFoundException {

        UserDTO userDTO = facade.getUserByName(u1.getUserName());

        assertEquals("John Dillermand", userDTO.getUserName());
    }

    @Test
    public void testDeleteUser() throws NotFoundException {

        UserDTO UserDTO = facade.deleteUser(u1.getUserName());
        assertEquals(2, facade.getUserCount());

    }
}
