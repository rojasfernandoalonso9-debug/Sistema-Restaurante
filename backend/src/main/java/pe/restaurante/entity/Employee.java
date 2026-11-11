package pe.restaurante.entity;
import jakarta.persistence.*;
@Entity @Table(name="employees")
public class Employee {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @OneToOne(fetch=FetchType.LAZY) @JoinColumn(name="user_id",unique=true) private RestaurantUser user;
 @Column(name="full_name",nullable=false,length=160) private String fullName;
 @Column(name="job_title",nullable=false,length=100) private String jobTitle;
 @Column(length=40) private String phone;
 @Column(name="employment_status",nullable=false,length=20) private String employmentStatus="ACTIVE";
 public Long getId(){return id;} public RestaurantUser getUser(){return user;} public void setUser(RestaurantUser v){user=v;}
 public String getFullName(){return fullName;} public void setFullName(String v){fullName=v;} public String getJobTitle(){return jobTitle;} public void setJobTitle(String v){jobTitle=v;}
 public String getPhone(){return phone;} public void setPhone(String v){phone=v;} public String getEmploymentStatus(){return employmentStatus;} public void setEmploymentStatus(String v){employmentStatus=v;}
}
