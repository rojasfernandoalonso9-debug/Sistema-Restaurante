package pe.restaurante.dto;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
public record MovementRequest(@Pattern(regexp="IN|OUT") String movementType,@NotNull @DecimalMin(value="0.001") BigDecimal quantity,@Size(max=500) String note) {}
