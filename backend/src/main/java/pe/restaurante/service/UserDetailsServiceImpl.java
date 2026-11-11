package pe.restaurante.service;
import pe.restaurante.repository.RestaurantUserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.List;
@Service public class UserDetailsServiceImpl implements UserDetailsService {
    private final RestaurantUserRepository users;
    public UserDetailsServiceImpl(RestaurantUserRepository users){this.users=users;}
    public UserDetails loadUserByUsername(String email){var user=users.findByEmailIgnoreCase(email).filter(u->u.isActive()).orElseThrow(()->new UsernameNotFoundException("Usuario no encontrado"));return new org.springframework.security.core.userdetails.User(user.getEmail(),user.getPasswordHash(),List.of(new SimpleGrantedAuthority("ROLE_"+user.getRole())));}
}
