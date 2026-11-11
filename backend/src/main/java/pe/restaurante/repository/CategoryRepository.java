package pe.restaurante.repository;
import pe.restaurante.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CategoryRepository extends JpaRepository<Category,Long>{boolean existsByNameIgnoreCase(String name);}
