package pe.restaurante.dto;
import jakarta.validation.constraints.*;
public record UserRequest(@NotBlank @Size(max=120) String name,@Email @NotBlank @Size(max=190) String email,@Pattern(regexp="^$|.{12,100}") String password,@Pattern(regexp="ADMIN|MANAGER|STAFF") String role) {}
