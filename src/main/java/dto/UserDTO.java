package dto;

import entities.Dog;
import entities.User;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserDTO {

    private String userName;
    private String password;
    private List<DogDTO> dogList = new ArrayList<>();
    
    

    
    public UserDTO(User user) {
        this.userName = user.getUserName();
        this.password = user.getUserPassword();
        user.getDogList().forEach((dog) -> { this.dogList.add(new DogDTO(dog));
        });
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public void setDogList (List <DogDTO> dogList){
        this.dogList = dogList;
        
    }



}
