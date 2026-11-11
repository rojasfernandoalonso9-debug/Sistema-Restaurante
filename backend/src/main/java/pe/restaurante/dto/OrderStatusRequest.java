package pe.restaurante.dto;
import jakarta.validation.constraints.Pattern;
public record OrderStatusRequest(@Pattern(regexp="PREPARING|READY|DELIVERED|CANCELLED") String status) {}
