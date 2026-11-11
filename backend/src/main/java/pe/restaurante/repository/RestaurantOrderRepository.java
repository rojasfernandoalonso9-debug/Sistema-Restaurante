package pe.restaurante.repository;
import pe.restaurante.entity.RestaurantOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.*;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.List;
public interface RestaurantOrderRepository extends JpaRepository<RestaurantOrder,Long>{List<RestaurantOrder> findAllByOrderByCreatedAtDesc();long countByStatusIn(java.util.Collection<String> statuses);boolean existsByTableId(Long tableId);@Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select o from RestaurantOrder o where o.id=:id") Optional<RestaurantOrder> findLockedById(Long id);}
