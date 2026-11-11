package pe.restaurante.controller;
import pe.restaurante.dto.*;
import pe.restaurante.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/employees") @PreAuthorize("hasAnyRole('ADMIN','MANAGER')") public class EmployeeController{
 private final EmployeeService service;public EmployeeController(EmployeeService s){service=s;}
 @GetMapping public List<EmployeeResponse> list(){return service.list();}@PostMapping @ResponseStatus(HttpStatus.CREATED) public EmployeeResponse create(@Valid @RequestBody EmployeeRequest r){return service.create(r);}
 @PutMapping("/{id}") public EmployeeResponse update(@PathVariable Long id,@Valid @RequestBody EmployeeRequest r){return service.update(id,r);}@DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id){service.delete(id);}
}
