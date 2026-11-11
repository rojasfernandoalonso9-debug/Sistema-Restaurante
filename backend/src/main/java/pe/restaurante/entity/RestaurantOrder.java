package pe.restaurante.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
@Entity @Table(name="restaurant_orders",indexes=@Index(name="idx_orders_status_date",columnList="status,created_at"))
public class RestaurantOrder {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="table_id") private DiningTable table;
 @Column(name="table_status_before_order",length=20) private String tableStatusBeforeOrder;
 @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="created_by",nullable=false) private RestaurantUser createdBy;
 @Column(nullable=false,length=20) private String status="PENDING";
 @Column(nullable=false,precision=12,scale=2) private BigDecimal total=BigDecimal.ZERO;
 @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt;
 @PrePersist void timestamp(){createdAt=Instant.now();}
 public Long getId(){return id;} public DiningTable getTable(){return table;} public void setTable(DiningTable v){table=v;}
 public String getTableStatusBeforeOrder(){return tableStatusBeforeOrder;} public void setTableStatusBeforeOrder(String v){tableStatusBeforeOrder=v;}
 public RestaurantUser getCreatedBy(){return createdBy;} public void setCreatedBy(RestaurantUser v){createdBy=v;} public String getStatus(){return status;} public void setStatus(String v){status=v;}
 public BigDecimal getTotal(){return total;} public void setTotal(BigDecimal v){total=v;} public Instant getCreatedAt(){return createdAt;}
}
