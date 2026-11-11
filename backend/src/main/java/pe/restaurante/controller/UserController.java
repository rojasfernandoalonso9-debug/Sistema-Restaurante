package pe.restaurante.controller;
import pe.restaurante.dto.*;
import pe.restaurante.entity.RestaurantUser;
import pe.restaurante.repository.RestaurantUserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
@RestController @RequestMapping("/api/users") @PreAuthorize("hasRole('ADMIN')") public class UserController {
 private final RestaurantUserRepository users;private final PasswordEncoder encoder;
 public UserController(RestaurantUserRepository users,PasswordEncoder encoder){this.users=users;this.encoder=encoder;}
 @GetMapping public List<UserResponse> list(){return users.findAll().stream().map(UserResponse::from).toList();}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) @Transactional public UserResponse create(@Valid @RequestBody UserRequest r){if(r.password()==null||r.password().length()<12)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"La contraseña debe tener al menos 12 caracteres");if(users.findByEmailIgnoreCase(r.email()).isPresent())throw new ResponseStatusException(HttpStatus.CONFLICT,"El correo ya está registrado");var u=new RestaurantUser();u.setName(r.name().trim());u.setEmail(r.email().toLowerCase());u.setRole(r.role()==null?"STAFF":r.role());u.setPasswordHash(encoder.encode(r.password()));return UserResponse.from(users.save(u));}
 @PutMapping("/{id}") @Transactional public UserResponse update(@PathVariable Long id,@Valid @RequestBody UserRequest r){var u=users.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));users.findByEmailIgnoreCase(r.email()).filter(other->!other.getId().equals(id)).ifPresent(other->{throw new ResponseStatusException(HttpStatus.CONFLICT,"El correo ya está registrado");});u.setName(r.name().trim());u.setEmail(r.email().toLowerCase());if(r.role()!=null)u.setRole(r.role());if(r.password()!=null&&!r.password().isBlank())u.setPasswordHash(encoder.encode(r.password()));return UserResponse.from(u);}
 @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) @Transactional public void delete(@PathVariable Long id){var u=users.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));if("ADMIN".equals(u.getRole())&&users.countByRole("ADMIN")<=1)throw new ResponseStatusException(HttpStatus.CONFLICT,"No puedes eliminar al único administrador");users.delete(u);}
}
