package pe.restaurante.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public record VoidSaleRequest(@NotBlank @Size(max=500) String reason) {}
