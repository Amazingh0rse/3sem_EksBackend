package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.DogDTO;
import dto.DogsDTO;

import entities.Dog;
import errorhandling.MissingInputException;
import errorhandling.NotFoundException;
import facades.DogFacade;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import utils.EMF_Creator;

@Path("dog")
public class DogResource {
    
    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    
    @Context
    private UriInfo context;
    
    private static final DogFacade FACADE =  DogFacade.getDogFacade(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getDogForAll() {
        return "{\"msg\":\"Hello from Addresses\"}";
    }

    //Just to verify if the database is setup
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("count")
    public String countDog() {

        EntityManager em = EMF.createEntityManager();
        try {
            TypedQuery<Dog> query = em.createQuery ("SELECT d from Dog d", Dog.class);
            List<Dog> dog = query.getResultList();
            return "[" + dog.size() + "]";
        } finally {
            em.close();
        }
    }

    
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("all")
    public String getAllDogs() throws NotFoundException {
        DogsDTO dDTO = FACADE.getAllDogs();
        return GSON.toJson(dDTO);
    }
    
    
//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    @Path("name/{name}")
//    public String getDogByName(@PathParam("name") String name) throws NotFoundException {
//        return GSON.toJson(FACADE.getDogByName(name));
//    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("id/{id}")
    public String getDogById(@PathParam("id") Long id) throws NotFoundException {
        return GSON.toJson(FACADE.getDogById(id));
    }
    
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public String addDog(String dog) throws MissingInputException {
        DogDTO d = GSON.fromJson(dog, DogDTO.class);
        DogDTO DogDTO = FACADE.addDog(d.getName(), d.getDateOfBirth(), d.getInfo(), d.getBreed());
        return GSON.toJson(DogDTO);
    }
    
    @DELETE
    @Path("removeDog/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response removeDogfromUser(@PathParam("id")long id) throws NotFoundException{
        
        FACADE.deleteDog(id);
        
        return Response.status(Response.Status.OK).entity("Dog removed from user OK").build();
    }  
    
    @PUT
    @Path("update")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response updateDog(String dog) throws NotFoundException {
        //DogDTO DogDTO = GSON.fromJson(dog, DogDTO.class);
        DogDTO dogDTO = GSON.fromJson(dog, DogDTO.class);
        FACADE.updateDog(dogDTO);        
        return Response.status(Response.Status.OK).entity("dog updated OK").build();
    }            

 
}
