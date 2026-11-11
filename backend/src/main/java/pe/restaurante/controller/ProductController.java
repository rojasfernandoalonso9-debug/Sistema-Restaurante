package pe.restaurante.controller;
import pe.restaurante.dto.*;
import pe.restaurante.service.ProductService;
import pe.restaurante.service.RecipeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/products") public class ProductController{
 private final ProductService service;private final RecipeService recipes;public ProductController(ProductService s,RecipeService r){service=s;recipes=r;}
 @GetMapping public List<ProductResponse> list(){return service.list();}
 @PostMapping @PreAuthorize("hasAnyRole('ADMIN','MANAGER')") @ResponseStatus(HttpStatus.CREATED) public ProductResponse create(@Valid @RequestBody ProductRequest r){return service.create(r);}
 @PutMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','MANAGER')") public ProductResponse update(@PathVariable Long id,@Valid @RequestBody ProductRequest r){return service.update(id,r);}
 @DeleteMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','MANAGER')") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id){service.delete(id);}
 @GetMapping("/{id}/recipe") public List<RecipeIngredientResponse> recipe(@PathVariable Long id){return recipes.get(id);}
 @PutMapping("/{id}/recipe") @PreAuthorize("hasAnyRole('ADMIN','MANAGER')") public List<RecipeIngredientResponse> recipe(@PathVariable Long id,@Valid @RequestBody RecipeRequest r){return recipes.replace(id,r);}
}
