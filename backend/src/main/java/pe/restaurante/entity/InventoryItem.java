package pe.restaurante.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
@Entity @Table(name="inventory_items")
public class InventoryItem {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,unique=true,length=160) private String name;
 @Column(nullable=false,length=30) private String unit;
 @Column(nullable=false,precision=12,scale=3) private BigDecimal quantity=BigDecimal.ZERO;
 @Column(name="minimum_quantity",nullable=false,precision=12,scale=3) private BigDecimal minimumQuantity=BigDecimal.ZERO;
 @Column(name="updated_at",nullable=false) private Instant updatedAt;
 @PrePersist @PreUpdate void timestamp(){updatedAt=Instant.now();}
 public Long getId(){return id;} public String getName(){return name;} public void setName(String v){name=v;} public String getUnit(){return unit;} public void setUnit(String v){unit=v;}
 public BigDecimal getQuantity(){return quantity;} public void setQuantity(BigDecimal v){quantity=v;} public BigDecimal getMinimumQuantity(){return minimumQuantity;} public void setMinimumQuantity(BigDecimal v){minimumQuantity=v;}
}
