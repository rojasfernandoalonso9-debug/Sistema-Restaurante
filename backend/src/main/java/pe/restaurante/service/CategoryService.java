package pe.restaurante.service;
import pe.restaurante.dto.CategoryRequest;
import pe.restaurante.entity.Category;
import pe.restaurante.repository.CategoryRepository;
import pe.restaurante.repository.RestaurantProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
@Service public class CategoryService{
 private final CategoryRepository categories;private final RestaurantProductRepository products;
 public CategoryService(CategoryRepository c,RestaurantProductRepository p){categories=c;products=p;}
 public List<Category> list(){return categories.findAll();}
 @Transactional public Category create(CategoryRequest r){if(categories.existsByNameIgnoreCase(r.name().trim()))throw new ResponseStatusException(HttpStatus.CONFLICT,"La categoría ya existe");var c=new Category();c.setName(r.name().trim());c.setDescription(r.description());return categories.save(c);}
 @Transactional public Category update(Long id,CategoryRequest r){var c=categories.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));c.setName(r.name().trim());c.setDescription(r.description());return c;}
 @Transactional public void delete(Long id){var c=categories.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));if(products.countByCategoryId(id)>0)throw new ResponseStatusException(HttpStatus.CONFLICT,"La categoría tiene productos asociados");categories.delete(c);}
}
