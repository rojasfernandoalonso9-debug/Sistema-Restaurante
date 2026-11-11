package pe.restaurante.dto;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
public record ProductRequest(@NotBlank @Size(max=160) String name,@Size(max=1000) String description,@NotNull @PositiveOrZero BigDecimal price,@NotNull Long categoryId,String imageUrl,Boolean available) {}
