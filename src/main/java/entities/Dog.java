package entities;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "dog")
@NamedQuery(name = "Dog.deleteAllRows", query = "DELETE from Dog")
public class Dog implements Serializable {

    private static final long serialVersionUID = 1L;

    
            
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "dateOfBirth")
    private LocalDate dateOfBirth;
    
    @Column (name = "info", length = 255)
    private String info;
    
    @Column (name = "breed", length = 255)
    private String breed;
 
    @JoinColumn (name = "userName")
    @ManyToOne (cascade = CascadeType.PERSIST)
    private User user;
    
   
    public Dog() {
    }
    
    public Dog(String name, LocalDate dateOfBirth, String info, String breed ) {
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.info = info;
        this.breed = breed;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Dog)) {
            return false;
        }
        Dog other = (Dog) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Dog[ id=" + id + " ]";
    }

}
