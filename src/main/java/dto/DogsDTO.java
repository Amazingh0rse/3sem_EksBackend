
package dto;


import entities.Dog;
import java.util.ArrayList;
import java.util.List;


public class DogsDTO {
    
    List<DogDTO> all = new ArrayList();

    public DogsDTO(List<Dog> dogEntities) {
        dogEntities.forEach((a) -> {
            all.add(new DogDTO(a));
        });
    }

    public List<DogDTO> getAll() {
        return all;
    }

    public void setAll(List<DogDTO> all) {
        this.all = all;
    }
    
}
