package pe.restaurante.dto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;
public record OrderRequest(Long tableId,@NotEmpty @Size(max=100) List<@Valid OrderLineRequest> items) {
 public record OrderLineRequest(@NotNull Long productId,@Min(1) @Max(1000) int quantity) {}
}
