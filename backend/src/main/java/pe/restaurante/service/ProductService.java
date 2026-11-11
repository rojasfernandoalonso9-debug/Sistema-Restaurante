package pe.restaurante.service;
import pe.restaurante.dto.*;
import pe.restaurante.entity.RestaurantProduct;
import pe.restaurante.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
@Service public class ProductService{
 private final RestaurantProductRepository products;private final CategoryRepository categories;
 public ProductService(RestaurantProductRepository p,CategoryRepository c){products=p;categories=c;}
 @Transactional(readOnly=true) public List<ProductResponse> list(){return products.findAllByOrderByNameAsc().stream().map(ProductResponse::from).toList();}
 @Transactional public ProductResponse create(ProductRequest r){var p=new RestaurantProduct();apply(p,r);return ProductResponse.from(products.save(p));}
 @Transactional public ProductResponse update(Long id,ProductRequest r){var p=products.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Producto no encontrado"));apply(p,r);return ProductResponse.from(p);}
 @Transactional public void delete(Long id){var p=products.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));p.setAvailable(false);}
 private void apply(RestaurantProduct p,ProductRequest r){p.setName(r.name().trim());p.setDescription(r.description());p.setPrice(r.price());p.setImageUrl(r.imageUrl());p.setAvailable(r.available()==null||r.available());p.setCategory(categories.findById(r.categoryId()).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST,"Categoría inválida")));}
}
