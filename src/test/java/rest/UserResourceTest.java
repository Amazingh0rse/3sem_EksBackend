package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.UserDTO;
import entities.Dog;

import entities.Role;
import entities.User;
import facades.UserFacade;

import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.parsing.Parser;
import java.net.URI;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import static org.hamcrest.Matchers.equalTo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Disabled;
import security.errorhandling.AuthenticationException;
import utils.EMF_Creator;

@Disabled
public class UserResourceTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";

    private User u1, u2, u3;
    private Role r1, r2;
    private Dog d1, d2, d3;
   

    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final UserFacade FACADE = UserFacade.getUserFacade(emf);

    static HttpServer startServer() {
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

    @BeforeAll
    public static void setUpClass() {
        //This method must be called before you request the EntityManagerFactory
        EMF_Creator.startREST_TestWithDB();
        emf = EMF_Creator.createEntityManagerFactoryForTest();

        httpServer = startServer();
        //Setup RestAssured
        RestAssured.baseURI = SERVER_URL;
        RestAssured.port = SERVER_PORT;
        RestAssured.defaultParser = Parser.JSON;
    }

    @AfterAll
    public static void closeTestServer() {
        //System.in.read();
        //Don't forget this, if you called its counterpart in @BeforeAll
        EMF_Creator.endREST_TestWithDB();
        httpServer.shutdownNow();
    }

    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
            u1 = new User("John Dillermand", "supersecretpassword");
            u2 = new User("Niels Buckingham", "supersecretpassword");
            u3 = new User("Robert Dølhus", "supersecretpassword");

            d1 = new Dog("Pølse", "1989/12/18", "This is my Wiener", "mexicanhairless");
            d2 = new Dog("Løg", "2010/5/19", "Min helt egen chow!", "chow");
            d3 = new Dog("Sigurd", "2016/2/3", "Sigurd Elsker bjørnetime!", "husky");
           
            
            r1 = new Role("user");
            r2 = new Role("admin");

            u1.addDog(d1);
            u2.addDog(d2);
            u2.addDog(d3);
  
            u1.addRole(r1);
            u2.addRole(r2);
        try {
            em.getTransaction().begin();
            em.createNamedQuery("User.deleteAllRows").executeUpdate();
            em.createNamedQuery("Role.deleteAllRows").executeUpdate();
            em.createNamedQuery("Dog.deleteAllRows").executeUpdate();
            em.persist(u1);
            em.persist(u2);
            em.persist(u3);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void testServerIsUp() {
        System.out.println("Testing is server UP");
        given()
                .when()
                .get("/user")
                .then().statusCode(200);
    }

    /**
     *
     */
    @Test
    public void testCount() {
        given()
                .contentType("application/json")
                .get("/user/count").then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body(equalTo("[" + 3 + "]"));
    }

    /**
     * Test of getInfoForAll method, of class PersonResource.
     */
    @Test
    public void testGetInfoForAll() {
        given()
                .contentType("application/json")
                .get("/user").then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body(equalTo("{\"msg\":\"Hello anonymous\"}"));
    }

    /**
     * Test of getFromUser method, of class PersonResource.
     */
    @Test
    public void testGetFromUser() {
        given()
                .when()
                .get("user/user/")
                .then().statusCode(403);
    }

    /**
     * Test of getFromAdmin method, of class PersonResource.
     */
    @Test
    public void testGetFromAdmin() {
        given()
                .when()
                .get("user/admin/")
                .then().statusCode(403);
    }

    @Test
    public void testGetUsersByName() {
        given()
                .when()
                .get("user/user/John Dillermand")
                .then().statusCode(403);
    }
    
     @Test
    public void testgetAllUsers() {
        given()
                .when()
                .get("user/all")
                .then().statusCode(403);
    }
    
    
    
    /**
     * Test of addUser method, of class PersonResource.
     */

    

    
    @Disabled
    @Test
    public void testAddUser() {
        //String postString = "{\"email\":\"johnjohn@hotmail.com\",\"password\":\"hunter2\",\"phone\":\"46791364\",\"firstName\":\"John\",\"lastName\":\"Langhals\"}";
        given()
                .contentType("application/json")
                .body(new UserDTO(u1))
                .when()
                .post("info")
                .then()
                .statusCode(HttpStatus.OK_200.getStatusCode());
        //.body("userID", equalTo("testperson1@hotmail.com"));
    }
    
    

}
