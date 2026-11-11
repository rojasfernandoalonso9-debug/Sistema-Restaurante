package pe.restaurante.dto;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
public record RecordRequest(@NotBlank @Size(max=160) String name, @Size(max=1000) String description, @Size(max=80) String status, @PositiveOrZero BigDecimal price, @PositiveOrZero Integer stock, @Email @Size(max=190) String email, @Size(max=30) String role, @PositiveOrZero Integer capacity, @Size(max=80) String tableName) {}
