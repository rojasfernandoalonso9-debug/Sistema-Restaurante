package pe.restaurante.repository;
import pe.restaurante.entity.ProductIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ProductIngredientRepository extends JpaRepository<ProductIngredient,Long>{List<ProductIngredient> findAllByProductIdOrderById(Long productId);void deleteAllByProductId(Long productId);}
