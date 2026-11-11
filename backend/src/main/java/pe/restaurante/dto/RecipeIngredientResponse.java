package pe.restaurante.dto;
import pe.restaurante.entity.ProductIngredient;
import java.math.BigDecimal;
public record RecipeIngredientResponse(Long inventoryItemId,String name,String unit,BigDecimal quantityPerProduct){
 public static RecipeIngredientResponse from(ProductIngredient i){return new RecipeIngredientResponse(i.getItem().getId(),i.getItem().getName(),i.getItem().getUnit(),i.getQuantityPerProduct());}
}
