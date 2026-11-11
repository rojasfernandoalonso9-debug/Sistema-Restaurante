package pe.restaurante.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
@Entity @Table(name="sales",indexes=@Index(name="idx_sales_date_status",columnList="created_at,status"))
public class RestaurantSale {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @OneToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="order_id",nullable=false,unique=true) private RestaurantOrder order;
 @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="user_id",nullable=false) private RestaurantUser user;
 @Column(name="payment_method",nullable=false,length=20) private String paymentMethod;
 @Column(nullable=false,precision=12,scale=2) private BigDecimal total;
 @Column(nullable=false,length=20) private String status="COMPLETED";
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="voided_by") private RestaurantUser voidedBy;
 @Column(name="void_reason",length=500) private String voidReason;
 @Column(name="voided_at") private Instant voidedAt;
 @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt;
 @PrePersist void timestamp(){createdAt=Instant.now();}
 public Long getId(){return id;} public RestaurantOrder getOrder(){return order;} public void setOrder(RestaurantOrder v){order=v;} public RestaurantUser getUser(){return user;} public void setUser(RestaurantUser v){user=v;}
 public String getPaymentMethod(){return paymentMethod;} public void setPaymentMethod(String v){paymentMethod=v;} public BigDecimal getTotal(){return total;} public void setTotal(BigDecimal v){total=v;}
 public String getStatus(){return status;} public void setStatus(String v){status=v;} public RestaurantUser getVoidedBy(){return voidedBy;} public void setVoidedBy(RestaurantUser v){voidedBy=v;}
 public String getVoidReason(){return voidReason;} public void setVoidReason(String v){voidReason=v;} public Instant getVoidedAt(){return voidedAt;} public void setVoidedAt(Instant v){voidedAt=v;} public Instant getCreatedAt(){return createdAt;}
}
