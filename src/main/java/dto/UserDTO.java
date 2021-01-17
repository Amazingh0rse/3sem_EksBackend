package dto;

import entities.Dog;
import entities.User;
import java.util.ArrayList;
import java.util.List;

public class UserDTO {

    private String userName;
    private String password;
    private List<DogDTO> dogList = new ArrayList<>();
    
    

    
    public UserDTO(User user) {
        this.userName = user.getUserName();
        this.password = user.getUserPassword();
        
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
