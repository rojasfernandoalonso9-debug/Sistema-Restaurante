package pe.restaurante.repository;
import pe.restaurante.entity.RestaurantUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface RestaurantUserRepository extends JpaRepository<RestaurantUser,Long> {
    Optional<RestaurantUser> findByEmailIgnoreCase(String email);
    long countByRole(String role);
}
