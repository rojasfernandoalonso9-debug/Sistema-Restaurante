package pe.restaurante.repository;
import pe.restaurante.entity.DiningTable;
import org.springframework.data.jpa.repository.*;
import jakarta.persistence.LockModeType;
import java.util.*;
public interface DiningTableRepository extends JpaRepository<DiningTable,Long>{List<DiningTable> findAllByOrderByTableNumberAsc();long countByStatus(String status);@Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select t from DiningTable t where t.id=:id") Optional<DiningTable> findLockedById(Long id);}
