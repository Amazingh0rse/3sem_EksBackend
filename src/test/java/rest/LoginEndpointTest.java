package rest;

import entities.Role;
import entities.User;
import entities.Dog;

import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import java.net.URI;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import static org.hamcrest.Matchers.equalTo;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator;

@Disabled
public class LoginEndpointTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";

    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;

    static HttpServer startServer() {
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

    private User u1, u2, u3;
    private Role r1, r2;
    private Dog d1, d2, d3, d4;

    

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
        //Don't forget this, if you called its counterpart in @BeforeAll
        EMF_Creator.endREST_TestWithDB();

        httpServer.shutdownNow();
    }

    // Setup the DataBase (used by the test-server and this test) in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the EntityClass used below to use YOUR OWN (renamed) Entity class
    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            //Delete existing users and roles to get a "fresh" database
            //em.createQuery("delete from User").executeUpdate();
            // em.createQuery("delete from Role").executeUpdate();
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
            u3.addRole(r2);
            u3.addRole(r1);
            
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

    //This is how we hold on to the token after login, similar to that a client must store the token somewhere
    private static String securityToken;

    //Utility method to login and set the returned securityToken
    private static void login(String role, String password) {
        String json = String.format("{username: \"%s\", password: \"%s\"}", role, password);
        securityToken = given()
                .contentType("application/json")
                .body(json)
                //.when().post("/api/login")
                .when().post("/login")
                .then()
                .extract().path("token");
        //System.out.println("TOKEN ---> " + securityToken);
    }

    private void logOut() {
        securityToken = null;
    }

    @Test
    public void serverIsRunning() {
        given().when().get("/user").then().statusCode(200);
    }

    @Test
    public void testRestNoAuthenticationRequired() {
        given()
                .contentType("application/json")
                .when()
                .get("/user/").then()
                .statusCode(200)
                .body("msg", equalTo("Hello anonymous"));
    }

    @Test
    public void testRestForAdmin() {
        login("Niels Buckingham", "supersecretpassword");
        given()
                .contentType("application/json")
                .accept(ContentType.JSON)
                .header("x-access-token", securityToken)
                .when()
                .get("/user/admin").then()
                .statusCode(200)
                .body("msg", equalTo("Hello to (admin) User: " + u2.getUserName()));
    }

    @Test
    public void testRestForUser() {
        login("John Dillermand", "supersecretpassword");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/user/user").then()
                .statusCode(200)
                .body("msg", equalTo("Hello to User: " + u1.getUserName()));
    }

    @Test
    public void testAutorizedUserCannotAccesAdminPage() {
        login("John Dillermand", "supersecretpassword");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/user/admin").then() //Call Admin endpoint as user
                .statusCode(401);
    }

    @Test
    public void testAutorizedAdminCannotAccesUserPage() {
        login("Niels Buckingham", "supersecretpassword");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/user/user").then() //Call Person endpoint as Admin
                .statusCode(401);
    }

    @Test
    public void testRestForMultiRole1() {
        login("Robert Dølhus", "supersecretpassword");
        given()
                .contentType("application/json")
                .accept(ContentType.JSON)
                .header("x-access-token", securityToken)
                .when()
                .get("/user/admin").then()
                .statusCode(200)
                .body("msg", equalTo("Hello to (admin) User: " + u3.getUserName()));
    }

    @Test
    public void testRestForMultiRole2() {
        login("Robert Dølhus", "supersecretpassword");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/user/user").then()
                .statusCode(200)
                .body("msg", equalTo("Hello to User: " + u3.getUserName()));
    }

    @Test
    public void userNotAuthenticated() {
        logOut();
        given()
                .contentType("application/json")
                .when()
                .get("/user/user").then()
                .statusCode(403)
                .body("code", equalTo(403))
                .body("message", equalTo("Not authenticated - do login"));
    }

    @Test
    public void adminNotAuthenticated() {
        logOut();
        given()
                .contentType("application/json")
                .when()
                .get("/user/user").then()
                .statusCode(403)
                .body("code", equalTo(403))
                .body("message", equalTo("Not authenticated - do login"));
    }

}
