package pe.restaurante.entity;
import jakarta.persistence.*;
@Entity @Table(name="dining_tables")
public class DiningTable {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(name="table_number",nullable=false,unique=true,length=30) private String tableNumber;
 @Column(nullable=false) private int capacity;
 @Column(nullable=false,length=20) private String status="AVAILABLE";
 @Version private long version;
 public Long getId(){return id;} public String getTableNumber(){return tableNumber;} public void setTableNumber(String v){tableNumber=v;}
 public int getCapacity(){return capacity;} public void setCapacity(int v){capacity=v;} public String getStatus(){return status;} public void setStatus(String v){status=v;}
}
