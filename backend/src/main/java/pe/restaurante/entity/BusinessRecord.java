package pe.restaurante.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name="business_records", indexes={@Index(name="idx_record_type", columnList="record_type"), @Index(name="idx_record_name", columnList="name")})
public class BusinessRecord {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(name="record_type", nullable=false, length=32) private String type;
    @Column(nullable=false, length=160) private String name;
    @Column(length=1000) private String description;
    @Column(length=80) private String status;
    @Column(precision=12, scale=2) private BigDecimal price;
    private Integer stock;
    @Column(name="created_at", nullable=false, updatable=false) private Instant createdAt;
    @Column(length=190) private String email;
    @Column(length=30) private String role;
    private Integer capacity;
    @Column(name="table_name",length=80) private String tableName;
    @PrePersist void createTimestamp() { createdAt=Instant.now(); }
    public Long getId(){return id;} public String getType(){return type;} public void setType(String type){this.type=type;}
    public String getName(){return name;} public void setName(String name){this.name=name;}
    public String getDescription(){return description;} public void setDescription(String description){this.description=description;}
    public String getStatus(){return status;} public void setStatus(String status){this.status=status;}
    public BigDecimal getPrice(){return price;} public void setPrice(BigDecimal price){this.price=price;}
    public Integer getStock(){return stock;} public void setStock(Integer stock){this.stock=stock;}
    public Instant getCreatedAt(){return createdAt;}
    public String getEmail(){return email;} public void setEmail(String email){this.email=email;}
    public String getRole(){return role;} public void setRole(String role){this.role=role;}
    public Integer getCapacity(){return capacity;} public void setCapacity(Integer capacity){this.capacity=capacity;}
    public String getTableName(){return tableName;} public void setTableName(String tableName){this.tableName=tableName;}
}
