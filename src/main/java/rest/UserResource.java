package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.UserDTO;
import dto.UsersDTO;
import entities.User;
import errorhandling.NotFoundException;
import facades.UserFacade;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.ws.rs.DELETE;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import utils.EMF_Creator;

@Path("user")
public class UserResource {
    
    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    @Context
    private UriInfo context;

    @Context
    SecurityContext securityContext;
    
    private static final UserFacade FACADE =  UserFacade.getUserFacade(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getInfoForAll() {
        return "{\"msg\":\"Hello anonymous\"}";
    }

    //Just to verify if the database is setup
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("count")
    public String allUsers() {

        EntityManager em = EMF.createEntityManager();
        try {
            TypedQuery<User> query = em.createQuery ("select u from User u", entities.User.class);
            List<User> users = query.getResultList();
            return "[" + users.size() + "]";
        } finally {
            em.close();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("user")
    @RolesAllowed("user")
    public String getFromUser() {
        String thisuser = securityContext.getUserPrincipal().getName();
        return "{\"msg\": \"Hello to User: " + thisuser + "\"}";
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("admin")
    @RolesAllowed("admin")
    public String getFromAdmin() {
        String thisuser = securityContext.getUserPrincipal().getName();
        return "{\"msg\": \"Hello to (admin) User: " + thisuser + "\"}";
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("username/{userName}")
    public String getUsersByName(@PathParam("userName") String userName) throws NotFoundException {

        return GSON.toJson(FACADE.getUserByName(userName));

    }
    
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("all")
    public String getAllUsers() throws NotFoundException {
        UsersDTO usDTO = FACADE.getAllUsers();
        return GSON.toJson(usDTO);
    }
    
    
//    @POST
//    @Produces({MediaType.APPLICATION_JSON})
//    @Consumes({MediaType.APPLICATION_JSON})
//    public UserDTO addUser(String user) throws Exception {
//        UserDTO u = GSON.fromJson(user, UserDTO.class);
//        UserDTO newUser = FACADE.(u);
//        return newUser;
//    }    
    
//    @PUT
//    @Path("person")
//    @Produces({MediaType.APPLICATION_JSON})
//    @Consumes({MediaType.APPLICATION_JSON})
//    public Response updateUser(String user) throws NotFoundException{
//        UserDTO userDTO = GSON.fromJson(user, UserDTO.class);
//        FACADE.updateUser(userDTO);
//        return Response.status(Response.Status.OK).entity("Person updated OK").build();
//    }    
    
    @DELETE
    @Path("delete/{userName}")
    @Produces({MediaType.APPLICATION_JSON})
    public String deleteUser(@PathParam("userName") String userName) throws NotFoundException {
        UserDTO userDelete = FACADE.deleteUser(userName);
        return GSON.toJson(userDelete);
    }
    
}