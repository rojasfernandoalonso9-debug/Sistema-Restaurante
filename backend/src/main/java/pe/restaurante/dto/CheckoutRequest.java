package pe.restaurante.dto;
import jakarta.validation.constraints.Pattern;
public record CheckoutRequest(@Pattern(regexp="CASH|CARD|TRANSFER|OTHER") String paymentMethod) {}
