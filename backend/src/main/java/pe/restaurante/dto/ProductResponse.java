package pe.restaurante.dto;
import pe.restaurante.entity.RestaurantProduct;
import java.math.BigDecimal;
public record ProductResponse(Long id,String name,String description,BigDecimal price,Long categoryId,String categoryName,String imageUrl,boolean available){
 public static ProductResponse from(RestaurantProduct p){return new ProductResponse(p.getId(),p.getName(),p.getDescription(),p.getPrice(),p.getCategory().getId(),p.getCategory().getName(),p.getImageUrl(),p.isAvailable());}
}
