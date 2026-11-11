package pe.restaurante.repository;
import pe.restaurante.entity.BusinessRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface BusinessRecordRepository extends JpaRepository<BusinessRecord,Long> {
    List<BusinessRecord> findAllByTypeOrderByIdDesc(String type);
    Optional<BusinessRecord> findByIdAndType(Long id,String type);
}
