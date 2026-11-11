package pe.restaurante.controller;
import pe.restaurante.dto.LoginRequest;
import pe.restaurante.repository.RestaurantUserRepository;
import pe.restaurante.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import pe.restaurante.dto.SetupRequest;
import pe.restaurante.entity.RestaurantUser;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController @RequestMapping("/api/auth") public class AuthController {
    private final AuthenticationManager manager;private final JwtService jwt;private final RestaurantUserRepository users;private final PasswordEncoder encoder;
    public AuthController(AuthenticationManager manager,JwtService jwt,RestaurantUserRepository users,PasswordEncoder encoder){this.manager=manager;this.jwt=jwt;this.users=users;this.encoder=encoder;}
    @PostMapping("/login") public Map<String,Object> login(@Valid @RequestBody LoginRequest request){manager.authenticate(new UsernamePasswordAuthenticationToken(request.email(),request.password()));var user=users.findByEmailIgnoreCase(request.email()).orElseThrow();return Map.of("token",jwt.issue(user.getEmail()),"name",user.getName(),"email",user.getEmail(),"role",user.getRole());}
    @PostMapping("/setup") @org.springframework.transaction.annotation.Transactional public Map<String,Object> setup(@Valid @RequestBody SetupRequest request){if(users.count()!=0)throw new ResponseStatusException(HttpStatus.CONFLICT,"El administrador inicial ya fue creado");var user=new RestaurantUser();user.setName(request.name());user.setEmail(request.email().toLowerCase());user.setRole("ADMIN");user.setPasswordHash(encoder.encode(request.password()));users.save(user);return Map.of("message","Administrador inicial creado. Inicia sesión para continuar.");}
}
