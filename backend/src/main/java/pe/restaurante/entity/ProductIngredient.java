package pe.restaurante.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
@Entity @Table(name="product_ingredients", uniqueConstraints=@UniqueConstraint(name="uk_recipe_product_item", columnNames={"product_id","inventory_item_id"}))
public class ProductIngredient {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="product_id",nullable=false) private RestaurantProduct product;
 @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="inventory_item_id",nullable=false) private InventoryItem item;
 @Column(name="quantity_per_product",nullable=false,precision=12,scale=3) private BigDecimal quantityPerProduct;
 public Long getId(){return id;} public RestaurantProduct getProduct(){return product;} public void setProduct(RestaurantProduct v){product=v;}
 public InventoryItem getItem(){return item;} public void setItem(InventoryItem v){item=v;} public BigDecimal getQuantityPerProduct(){return quantityPerProduct;} public void setQuantityPerProduct(BigDecimal v){quantityPerProduct=v;}
}
