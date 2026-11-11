package pe.restaurante.dto;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
public record InventoryRequest(@NotBlank @Size(max=160) String name,@NotBlank @Size(max=30) String unit,@NotNull @PositiveOrZero BigDecimal minimumQuantity) {}
