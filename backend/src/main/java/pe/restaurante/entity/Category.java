package pe.restaurante.entity;
import jakarta.persistence.*;
@Entity @Table(name="categories")
public class Category {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,unique=true,length=100) private String name;
 @Column(length=500) private String description;
 @Column(nullable=false) private boolean active=true;
 public Long getId(){return id;} public String getName(){return name;} public void setName(String value){name=value;}
 public String getDescription(){return description;} public void setDescription(String value){description=value;}
 public boolean isActive(){return active;} public void setActive(boolean value){active=value;}
}
