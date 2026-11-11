package pe.restaurante.repository;
import pe.restaurante.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface EmployeeRepository extends JpaRepository<Employee,Long>{List<Employee> findAllByOrderByFullNameAsc();boolean existsByUserId(Long userId);boolean existsByUserIdAndIdNot(Long userId,Long id);}
