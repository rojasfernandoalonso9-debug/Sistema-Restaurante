package pe.restaurante.controller;
import pe.restaurante.dto.*;
import pe.restaurante.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/orders") public class OrderController{
 private final OrderService service;public OrderController(OrderService s){service=s;}
 @GetMapping public List<OrderResponse> list(){return service.list();}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) public OrderResponse create(@Valid @RequestBody OrderRequest r,Authentication auth){return service.create(r,auth.getName());}
 @PutMapping("/{id}/items") public OrderResponse replace(@PathVariable Long id,@Valid @RequestBody OrderRequest r){return service.replace(id,r);}
 @PatchMapping("/{id}/status") public OrderResponse status(@PathVariable Long id,@Valid @RequestBody OrderStatusRequest r,Authentication auth){return service.changeStatus(id,r.status(),auth.getName());}
}
