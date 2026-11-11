package pe.restaurante.dto;
import pe.restaurante.entity.InventoryMovement;
import java.math.BigDecimal;
import java.time.Instant;
public record MovementResponse(Long id,Long itemId,String itemName,String type,BigDecimal quantity,String note,String user,Instant createdAt) {
 public static MovementResponse from(InventoryMovement m){return new MovementResponse(m.getId(),m.getItem().getId(),m.getItem().getName(),m.getMovementType(),m.getQuantity(),m.getNote(),m.getUser().getName(),m.getCreatedAt());}
}
