package pe.restaurante.dto;
import pe.restaurante.entity.RestaurantUser;
public record UserResponse(Long id,String name,String email,String role,boolean active){public static UserResponse from(RestaurantUser u){return new UserResponse(u.getId(),u.getName(),u.getEmail(),u.getRole(),u.isActive());}}
