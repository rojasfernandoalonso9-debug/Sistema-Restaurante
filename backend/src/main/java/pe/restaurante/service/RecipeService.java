package pe.restaurante.service;
import pe.restaurante.dto.*;
import pe.restaurante.entity.ProductIngredient;
import pe.restaurante.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;
@Service public class RecipeService{
 private final RestaurantProductRepository products;private final InventoryItemRepository inventory;private final ProductIngredientRepository ingredients;
 public RecipeService(RestaurantProductRepository p,InventoryItemRepository i,ProductIngredientRepository r){products=p;inventory=i;ingredients=r;}
 @Transactional(readOnly=true) public List<RecipeIngredientResponse> get(Long id){if(!products.existsById(id))throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Producto no encontrado");return ingredients.findAllByProductIdOrderById(id).stream().map(RecipeIngredientResponse::from).toList();}
 @Transactional public List<RecipeIngredientResponse> replace(Long id,RecipeRequest request){var product=products.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Producto no encontrado"));var ids=new HashSet<Long>();for(var line:request.ingredients()){if(!ids.add(line.inventoryItemId()))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No repitas insumos en la receta");if(line.quantityPerProduct().scale()>3)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"La cantidad admite hasta tres decimales");}var resolved=request.ingredients().stream().map(line->Map.entry(inventory.findById(line.inventoryItemId()).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST,"Insumo no encontrado")),line)).toList();ingredients.deleteAllByProductId(id);ingredients.flush();var saved=resolved.stream().map(entry->{var ingredient=new ProductIngredient();ingredient.setProduct(product);ingredient.setItem(entry.getKey());ingredient.setQuantityPerProduct(entry.getValue().quantityPerProduct());return ingredient;}).toList();ingredients.saveAll(saved);return saved.stream().map(RecipeIngredientResponse::from).toList();}
}
