package pe.restaurante.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity @Table(name="users", uniqueConstraints=@UniqueConstraint(name="uk_user_email", columnNames="email"))
public class RestaurantUser {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false,length=120) private String name;
    @Column(nullable=false,length=190) private String email;
    @Column(name="password_hash",nullable=false,length=100) private String passwordHash;
    @Column(nullable=false,length=30) private String role="STAFF";
    @Column(nullable=false) private boolean active=true;
    @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt;
    @PrePersist void timestamp(){createdAt=Instant.now();}
    public Long getId(){return id;} public String getName(){return name;} public void setName(String n){name=n;}
    public String getEmail(){return email;} public void setEmail(String e){email=e;}
    public String getPasswordHash(){return passwordHash;} public void setPasswordHash(String p){passwordHash=p;}
    public String getRole(){return role;} public void setRole(String r){role=r;}
    public boolean isActive(){return active;} public void setActive(boolean a){active=a;}
}
