package pe.restaurante.controller;
import pe.restaurante.dto.TableRequest;
import pe.restaurante.entity.DiningTable;
import pe.restaurante.service.DiningTableService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/tables") public class DiningTableController{
 private final DiningTableService service;public DiningTableController(DiningTableService s){service=s;}
 @GetMapping public List<DiningTable> list(){return service.list();}
 @PostMapping @PreAuthorize("hasAnyRole('ADMIN','MANAGER')") @ResponseStatus(HttpStatus.CREATED) public DiningTable create(@Valid @RequestBody TableRequest r){return service.create(r);}
 @PutMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','MANAGER')") public DiningTable update(@PathVariable Long id,@Valid @RequestBody TableRequest r){return service.update(id,r);}
 @DeleteMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','MANAGER')") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id){service.delete(id);}
}
