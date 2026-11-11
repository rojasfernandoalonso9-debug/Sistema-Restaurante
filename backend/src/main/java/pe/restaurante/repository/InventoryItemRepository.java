package pe.restaurante.repository;
import pe.restaurante.entity.InventoryItem;
import org.springframework.data.jpa.repository.*;
import jakarta.persistence.LockModeType;
import java.util.*;
public interface InventoryItemRepository extends JpaRepository<InventoryItem,Long>{List<InventoryItem> findAllByOrderByNameAsc();@Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select i from InventoryItem i where i.id=:id") Optional<InventoryItem> findLockedById(Long id);}
