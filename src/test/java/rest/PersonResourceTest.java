package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.PersonDTO;
import entities.Address;
import entities.Hobby;
import entities.Person;
import entities.Role;
import facades.PersonFacade;
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

//@Disabled
public class PersonResourceTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";

    private Person p1, p2, p3;
    private Role r1, r2;
    private Address a1, a2;
    private Hobby h1, h2;

    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final PersonFacade FACADE = PersonFacade.getPersonFacade(emf);

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
        r1 = new Role("user");
        r2 = new Role("admin");
        p1 = new Person("testperson1@hotmail.com", "secretpassword1", "11111111", "Kaj", "Bøjesen");
        p2 = new Person("testperson2@hotmail.com", "secretpassword2", "22222222", "Maren", "Maelk");
        p3 = new Person("testperson3@hotmail.com", "secretpassword3", "33333333", "David", "Eddings");
        a1 = new Address("Gøgeholmvej 2", "Helsingør", 3000);
        a2 = new Address("Slingrevænget 55", "Birkerød", 3460);
        h1 = new Hobby("Vandpolo", "Husk at holde vejret");
        h2 = new Hobby("Full Contact Petanque", "Bring your own equipment (And bandages!)");
        p1.addRole(r1);
        p1.addRole(r2);
        p2.addRole(r1);
        p3.addRole(r2);
        p1.setAddress(a1);
        p2.setAddress(a2);
        p3.setAddress(a1);
        p1.addHobby(h1);
        p2.addHobby(h2);
        p3.addHobby(h2);
        p3.addHobby(h1);
        try {
            em.getTransaction().begin();
            em.createNamedQuery("Person.deleteAllRows").executeUpdate();
            em.createNamedQuery("Role.deleteAllRows").executeUpdate();
            em.createNamedQuery("Address.deleteAllRows").executeUpdate();
            em.createNamedQuery("Hobby.deleteAllRows").executeUpdate();
            em.persist(p1);
            em.persist(p2);
            em.persist(p3);
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
                .get("/info")
                .then().statusCode(200);
    }

    /**
     *
     */
    @Test
    public void testCount() {
        given()
                .contentType("application/json")
                .get("/info/count").then()
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
                .get("/info").then()
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
                .get("info/user/")
                .then().statusCode(403);
    }

    /**
     * Test of getFromAdmin method, of class PersonResource.
     */
    @Test
    public void testGetFromAdmin() {
        given()
                .when()
                .get("info/admin/")
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
                .body(new PersonDTO(p1))
                .when()
                .post("info")
                .then()
                .statusCode(HttpStatus.OK_200.getStatusCode());
        //.body("userID", equalTo("testperson1@hotmail.com"));
    }

}
