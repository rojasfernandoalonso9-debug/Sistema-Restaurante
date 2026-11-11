package pe.restaurante.repository;
import pe.restaurante.entity.InventoryMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface InventoryMovementRepository extends JpaRepository<InventoryMovement,Long>{List<InventoryMovement> findAllByItemIdOrderByCreatedAtDesc(Long itemId);}
