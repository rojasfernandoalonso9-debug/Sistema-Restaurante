package pe.restaurante.repository;
import pe.restaurante.entity.RestaurantProduct;
import org.springframework.data.jpa.repository.*;
import jakarta.persistence.LockModeType;
import java.util.*;
public interface RestaurantProductRepository extends JpaRepository<RestaurantProduct,Long>{
 List<RestaurantProduct> findAllByOrderByNameAsc();
 long countByCategoryId(Long categoryId);
 long countByAvailableTrue();
 @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select p from RestaurantProduct p where p.id=:id") Optional<RestaurantProduct> findLockedById(Long id);
}
