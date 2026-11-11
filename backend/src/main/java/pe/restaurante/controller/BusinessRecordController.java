package pe.restaurante.controller;
import pe.restaurante.dto.RecordRequest;
import pe.restaurante.entity.BusinessRecord;
import pe.restaurante.service.BusinessRecordService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api")
public class BusinessRecordController {
    private final BusinessRecordService service;
    public BusinessRecordController(BusinessRecordService service){this.service=service;}
    @GetMapping("/{module}") public List<BusinessRecord> list(@PathVariable String module){return service.list(type(module));}
    @PostMapping("/{module}") @ResponseStatus(HttpStatus.CREATED) public BusinessRecord create(@PathVariable String module,@Valid @RequestBody RecordRequest request){return service.create(type(module),request);}
    @PutMapping("/{module}/{id}") public BusinessRecord update(@PathVariable String module,@PathVariable Long id,@Valid @RequestBody RecordRequest request){return service.update(type(module),id,request);}
    @DeleteMapping("/{module}/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable String module,@PathVariable Long id){service.delete(type(module),id);}
    private String type(String module){return switch(module){case "orders","tables","products","inventory","sales","reports"->module;default->throw new org.springframework.web.server.ResponseStatusException(HttpStatus.NOT_FOUND,"Módulo no encontrado");};}
}
