package pe.restaurante.controller;
import pe.restaurante.dto.CategoryRequest;
import pe.restaurante.entity.Category;
import pe.restaurante.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/categories") public class CategoryController{
 private final CategoryService service;public CategoryController(CategoryService s){service=s;}
 @GetMapping public List<Category> list(){return service.list();}
 @PostMapping @PreAuthorize("hasAnyRole('ADMIN','MANAGER')") @ResponseStatus(HttpStatus.CREATED) public Category create(@Valid @RequestBody CategoryRequest r){return service.create(r);}
 @PutMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','MANAGER')") public Category update(@PathVariable Long id,@Valid @RequestBody CategoryRequest r){return service.update(id,r);}
 @DeleteMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','MANAGER')") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id){service.delete(id);}
}
