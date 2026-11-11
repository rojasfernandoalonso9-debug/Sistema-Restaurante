package pe.restaurante.repository;
import pe.restaurante.entity.RestaurantSale;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
public interface RestaurantSaleRepository extends JpaRepository<RestaurantSale,Long>{
 Optional<RestaurantSale> findByOrderId(Long orderId);
 List<RestaurantSale> findAllByOrderByCreatedAtDesc();
 long countByStatus(String status);
 @Query("select coalesce(sum(s.total),0) from RestaurantSale s where s.status='COMPLETED' and s.createdAt>=:from and s.createdAt<:to") BigDecimal sumCompletedBetween(@Param("from") Instant from,@Param("to") Instant to);
 @Query("select coalesce(sum(s.total),0) from RestaurantSale s where s.status='COMPLETED' and s.createdAt>=:from") BigDecimal sumCompletedFrom(@Param("from") Instant from);
 @Query("select function('date',s.createdAt), coalesce(sum(s.total),0) from RestaurantSale s where s.status='COMPLETED' and s.createdAt>=:from group by function('date',s.createdAt)") List<Object[]> dailyTotals(@Param("from") Instant from);
}
