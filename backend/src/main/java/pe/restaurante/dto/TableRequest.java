package pe.restaurante.dto;
import jakarta.validation.constraints.*;
public record TableRequest(@NotBlank @Size(max=30) String tableNumber,@Min(1) @Max(100) int capacity,@Pattern(regexp="AVAILABLE|OCCUPIED|RESERVED") String status) {}
