package pe.restaurante.dto;
import jakarta.validation.constraints.*;
public record SetupRequest(@NotBlank @Size(max=120) String name,@Email @NotBlank @Size(max=190) String email,@NotBlank @Size(min=12,max=100) String password) {}
