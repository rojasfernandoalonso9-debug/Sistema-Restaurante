package pe.restaurante.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
@Entity @Table(name="products",indexes={@Index(name="idx_products_name",columnList="name"),@Index(name="idx_products_available",columnList="available")})
public class RestaurantProduct {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="category_id",nullable=false) private Category category;
 @Column(nullable=false,length=160) private String name;
 @Column(length=1000) private String description;
 @Column(nullable=false,precision=12,scale=2) private BigDecimal price;
 @Column(name="image_url",length=500) private String imageUrl;
 @Column(nullable=false) private boolean available=true;
 @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt;
 @PrePersist void timestamp(){createdAt=Instant.now();}
 public Long getId(){return id;} public Category getCategory(){return category;} public void setCategory(Category v){category=v;}
 public String getName(){return name;} public void setName(String v){name=v;} public String getDescription(){return description;} public void setDescription(String v){description=v;}
 public BigDecimal getPrice(){return price;} public void setPrice(BigDecimal v){price=v;} public String getImageUrl(){return imageUrl;} public void setImageUrl(String v){imageUrl=v;}
 public boolean isAvailable(){return available;} public void setAvailable(boolean v){available=v;}
}
