
package dto;

import entities.Dog;
import java.time.LocalDate;


public class DogDTO {
    
    private Long id;
    private String name;
    private LocalDate dateOfBirth;
    private String info;
    private String breed;

    public DogDTO(Dog dog) {
        this.id = dog.getId();
        this.name = dog.getName();
        this.dateOfBirth = dog.getDateOfBirth();
        this.info = dog.getInfo();
        this.breed = dog.getBreed();   
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

        
}
