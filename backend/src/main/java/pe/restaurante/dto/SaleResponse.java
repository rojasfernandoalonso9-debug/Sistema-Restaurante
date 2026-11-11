package pe.restaurante.dto;
import pe.restaurante.entity.RestaurantSale;
import java.math.BigDecimal;
import java.time.Instant;
public record SaleResponse(Long id,Long orderId,String paymentMethod,BigDecimal total,String status,String cashier,String voidReason,Instant voidedAt,Instant createdAt) {
 public static SaleResponse from(RestaurantSale s){return new SaleResponse(s.getId(),s.getOrder().getId(),s.getPaymentMethod(),s.getTotal(),s.getStatus(),s.getUser().getName(),s.getVoidReason(),s.getVoidedAt(),s.getCreatedAt());}
}
