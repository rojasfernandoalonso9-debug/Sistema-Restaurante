package pe.restaurante.controller;
import pe.restaurante.dto.*;
import pe.restaurante.entity.InventoryItem;
import pe.restaurante.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/inventory") public class InventoryController{
 private final InventoryService service;public InventoryController(InventoryService s){service=s;}
 @GetMapping public List<InventoryItem> list(){return service.list();}
 @PostMapping @PreAuthorize("hasAnyRole('ADMIN','MANAGER')") @ResponseStatus(HttpStatus.CREATED) public InventoryItem create(@Valid @RequestBody InventoryRequest r){return service.create(r);}
 @PutMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','MANAGER')") public InventoryItem update(@PathVariable Long id,@Valid @RequestBody InventoryRequest r){return service.update(id,r);}
 @DeleteMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','MANAGER')") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id){service.delete(id);}
 @PostMapping("/{id}/movements") @ResponseStatus(HttpStatus.CREATED) public MovementResponse movement(@PathVariable Long id,@Valid @RequestBody MovementRequest r,Authentication auth){return service.move(id,r,auth.getName());}
 @GetMapping("/{id}/movements") public List<MovementResponse> history(@PathVariable Long id){return service.history(id);}
}
