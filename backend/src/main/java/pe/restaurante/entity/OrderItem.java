package pe.restaurante.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
@Entity @Table(name="order_items")
public class OrderItem {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="order_id",nullable=false) private RestaurantOrder order;
 @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="product_id",nullable=false) private RestaurantProduct product;
 @Column(nullable=false) private int quantity;
 @Column(name="unit_price",nullable=false,precision=12,scale=2) private BigDecimal unitPrice;
 @Column(nullable=false,precision=12,scale=2) private BigDecimal subtotal;
 public Long getId(){return id;} public RestaurantOrder getOrder(){return order;} public void setOrder(RestaurantOrder v){order=v;}
 public RestaurantProduct getProduct(){return product;} public void setProduct(RestaurantProduct v){product=v;}
 public int getQuantity(){return quantity;} public void setQuantity(int v){quantity=v;} public BigDecimal getUnitPrice(){return unitPrice;} public void setUnitPrice(BigDecimal v){unitPrice=v;}
 public BigDecimal getSubtotal(){return subtotal;} public void setSubtotal(BigDecimal v){subtotal=v;}
}
