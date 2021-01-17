package entities;

import dto.DogDTO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author Amazingh0rse
 */

@Entity
@Table(name = "users")
@NamedQuery(name = "User.deleteAllRows", query = "DELETE from User")
public class User implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "userName", length = 50)
    private String userName;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "user_password")
    private String userPassword;
    
    //joins the user role table to users
    @JoinTable(name = "user_roles", joinColumns = {
        @JoinColumn(name = "userName", referencedColumnName = "userName")}, inverseJoinColumns = {
        @JoinColumn(name = "role_name", referencedColumnName = "role_name")})
    @ManyToMany (cascade = CascadeType.PERSIST)
    private List<Role> roleList = new ArrayList<>();
    
    
    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "user")
    private List<Dog> dogList = new ArrayList<>();

    public List<Dog> getDog() {
        return dogList;
    }

    public void setDog(List<Dog> dogList) {
        this.dogList = dogList;
    }
    
    public User() {
    }
    
     public User(String userName, String userPassword) {
        this.userName = userName;
        this.userPassword = BCrypt.hashpw(userPassword, BCrypt.gensalt(12));
     }
     
    //TODO Change when password is hashed
    public boolean verifyPassword(String pw) {
        return (BCrypt.checkpw(pw, this.userPassword));
    }

    
    //Getters & setters
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public List<String> getRolesAsStrings() {
        if (roleList.isEmpty()) {
            return null;
        }
        List<String> rolesAsStrings = new ArrayList<>();
        roleList.forEach((role) -> {
            rolesAsStrings.add(role.getRoleName());
        });
        return rolesAsStrings;
    }
    
    public List<Role> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<Role> roleList) {
        this.roleList = roleList;
    }
    
    public void addRole(Role userRole) {
        roleList.add(userRole);
    }

    public void addDog (Dog dog) {
        this.dogList.add(dog);
    }
    
    public List<Dog> getDogList() {
        return dogList;
    }

    public void setDogList(List<Dog> dogList) {
        this.dogList = dogList;
    }

    
}
