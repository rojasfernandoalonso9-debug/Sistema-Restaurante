package pe.restaurante.dto;
import jakarta.validation.constraints.*;
public record EmployeeRequest(@NotBlank @Size(max=160) String fullName,@NotBlank @Size(max=100) String jobTitle,@Size(max=40) String phone,Long userId,@Pattern(regexp="ACTIVE|INACTIVE") String employmentStatus) {}
