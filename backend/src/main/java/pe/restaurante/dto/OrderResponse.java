package pe.restaurante.dto;
import pe.restaurante.entity.RestaurantOrder;
import pe.restaurante.entity.OrderItem;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
public record OrderResponse(Long id,Long tableId,String tableName,String status,BigDecimal total,Instant createdAt,List<Line> items) {
 public record Line(Long productId,String productName,int quantity,BigDecimal unitPrice,BigDecimal subtotal) {}
 public static OrderResponse from(RestaurantOrder order,List<OrderItem> items){return new OrderResponse(order.getId(),order.getTable()==null?null:order.getTable().getId(),order.getTable()==null?null:order.getTable().getTableNumber(),order.getStatus(),order.getTotal(),order.getCreatedAt(),items.stream().map(i->new Line(i.getProduct().getId(),i.getProduct().getName(),i.getQuantity(),i.getUnitPrice(),i.getSubtotal())).toList());}
}
