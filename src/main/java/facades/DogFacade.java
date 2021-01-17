package facades;

import dto.DogDTO;
import dto.DogsDTO;
import entities.Dog;
import errorhandling.MissingInputException;
import errorhandling.NotFoundException;
import java.time.LocalDate;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class DogFacade {

    private static EntityManagerFactory emf;
    private static DogFacade instance;

    private DogFacade() {
    }

    /**
     *
     * @param _emf
     * @return the instance of this facade.
     */
    public static DogFacade getDogFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new DogFacade();
        }
        return instance;
    }

    public long getDogCount() {
        EntityManager em = emf.createEntityManager();
        try {
            long dogCount = (long) em.createQuery("SELECT COUNT(d) FROM Dog d").getSingleResult();
            return dogCount;
        } finally {
            em.close();
        }
    }

    public DogDTO getDogById(long id) throws NotFoundException {
        EntityManager em = emf.createEntityManager();

        Dog dog = em.find(Dog.class, id);
       
        if (dog == null) {
            throw new NotFoundException("No dog found with the given ID");
        } else {
            try {
                return new DogDTO(dog);
            } finally {
                em.close();
            }
        }
    }
    
    public DogDTO getDogByName(String name) throws NotFoundException {
        EntityManager em = emf.createEntityManager();

        Dog dog = em.find(Dog.class, name);
       
        if (dog == null) {
            throw new NotFoundException("No dog found with the given name");
        } else {
            try {
                return new DogDTO(dog);
            } finally {
                em.close();
            }
        }
    }

    
    public DogDTO addDog(String name, String dateOfBirth, String info, String breed) throws MissingInputException {
        if (isNameInValid(name, info, breed, dateOfBirth)) {
            throw new MissingInputException("Please fill out all fields");
        }
        EntityManager em = emf.createEntityManager();
        Dog dog = new Dog(name, dateOfBirth, info, breed);
        try {
            em.getTransaction().begin();
            em.persist(dog);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return new DogDTO(dog);
    }
    
    
    public DogsDTO getAllDogs() throws NotFoundException {

        EntityManager em = emf.createEntityManager();
        DogsDTO dsDTO;
        try {
            dsDTO = new DogsDTO(em.createQuery("SELECT d FROM Dog d").getResultList());
        } catch (Exception e) {
            throw new NotFoundException("No connection to the database");
        } finally {
            em.close();
        }
        return dsDTO;

    }
    private static boolean isNameInValid(String name, String info, String breed, String dateOfBirth) {
        return (name.length() == 0) || (info.length() == 0) || (breed.length() == 0) || (dateOfBirth.length() == 0);
    }
}
