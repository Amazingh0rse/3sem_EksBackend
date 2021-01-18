package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.DogDTO;
import dto.DogsDTO;

import entities.Dog;
import errorhandling.MissingInputException;
import errorhandling.NotFoundException;
import facades.DogFacade;
import java.io.IOException;
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
import utils.HttpUtils;

@Path("breed")
public class BreedResource {
    
    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    
    @Context
    private UriInfo context;
    
    //private static final DogFacade FACADE =  DogFacade.getDogFacade(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    List JsonString;
    String breeds;
        
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getAllBreeds() throws NotFoundException, IOException {
        
        String breeds = HttpUtils.fetchData("https://dog-info.cooljavascript.dk/api/breed");
        System.out.println(breeds);
        
        return breeds;
    }
    
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("bybreed/{breed}")
    public String getBreed(@PathParam("breed") String breed) throws NotFoundException, IOException {
        System.out.println(breed);
        String getbreed = HttpUtils.fetchData("https://dog-info.cooljavascript.dk/api/breed/"+breed);
        System.out.println();
        
        return getbreed;
    }
    
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("breedimg/{breed}")
    public String getBreedimg(@PathParam("breed") String breed) throws NotFoundException, IOException {
        System.out.println(breed);
        String getimg = HttpUtils.fetchData("https://dog-image.cooljavascript.dk/api/breed/random-image/"+breed);
        System.out.println();
        
        return getimg;
    }
    
}
