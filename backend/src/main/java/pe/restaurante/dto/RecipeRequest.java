package pe.restaurante.dto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;
public record RecipeRequest(@NotNull @Size(max=100) List<@Valid Ingredient> ingredients){
 public record Ingredient(@NotNull Long inventoryItemId,@NotNull @Positive BigDecimal quantityPerProduct){}
}
