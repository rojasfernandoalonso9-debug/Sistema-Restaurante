package pe.restaurante.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
@Entity @Table(name="inventory_movements",indexes=@Index(name="idx_movements_item_date",columnList="inventory_item_id,created_at"))
public class InventoryMovement {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="inventory_item_id",nullable=false) private InventoryItem item;
 @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="user_id",nullable=false) private RestaurantUser user;
 @Column(name="movement_type",nullable=false,length=20) private String movementType;
 @Column(nullable=false,precision=12,scale=3) private BigDecimal quantity;
 @Column(length=500) private String note;
 @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt;
 @PrePersist void timestamp(){createdAt=Instant.now();}
 public Long getId(){return id;} public InventoryItem getItem(){return item;} public void setItem(InventoryItem v){item=v;} public RestaurantUser getUser(){return user;} public void setUser(RestaurantUser v){user=v;}
 public String getMovementType(){return movementType;} public void setMovementType(String v){movementType=v;} public BigDecimal getQuantity(){return quantity;} public void setQuantity(BigDecimal v){quantity=v;}
 public String getNote(){return note;} public void setNote(String v){note=v;} public Instant getCreatedAt(){return createdAt;}
}
