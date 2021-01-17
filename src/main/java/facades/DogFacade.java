package facades;

import dto.DogDTO;
import dto.DogsDTO;
import dto.UserDTO;
import entities.Dog;
import entities.User;
import errorhandling.MissingInputException;
import errorhandling.NotFoundException;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.Cache;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;

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
    
    public DogDTO deleteDog(Long id) throws NotFoundException {
        EntityManager em = emf.createEntityManager();
        Dog dog = em.find(Dog.class, id);
        if (id == null) {
            throw new NotFoundException("Could not delete dog, provided id does not exist");
        } else {
            try {
                EntityTransaction txn = em.getTransaction();
                txn.begin();
                em.remove(dog);
                txn.commit();
            
                //attempt to get hibernate to clear cache
                Cache cache = emf.getCache();
                cache.evictAll();
                
            } finally {
                em.close();
            }
            return new DogDTO(dog);
        }
    }
    
     public void updateDog(DogDTO d) throws NotFoundException {
        EntityManager em = emf.createEntityManager();
        Dog dog = em.find(Dog.class, d.getId());
        if (dog== null) {
            throw new NotFoundException("No dog found");
        }
        dog.setName(d.getName());
        dog.setBreed(d.getBreed());
        dog.setDateOfBirth(d.getDateOfBirth());
        dog.setInfo(d.getInfo());
     
        try {
            em.getTransaction().begin();
            em.merge(dog);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
    
    
    
    private static boolean isNameInValid(String name, String info, String breed, String dateOfBirth) {
        return (name.length() == 0) || (info.length() == 0) || (breed.length() == 0) || (dateOfBirth.length() == 0);
    }
}


