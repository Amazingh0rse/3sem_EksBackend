
package dto;

import entities.User;
import java.util.ArrayList;
import java.util.List;


public class UsersDTO {
    
    List<UserDTO> all = new ArrayList();

    public UsersDTO(List<User> commentEntities) {
        commentEntities.forEach((user) -> {
            all.add(new UserDTO(user));
        });
    }

    public List<UserDTO> getAll() {
        return all;
    }

    public void setAll(List<UserDTO> all) {
        this.all = all;
    }
    
}
