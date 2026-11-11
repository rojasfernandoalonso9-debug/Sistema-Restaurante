package pe.restaurante.controller;
import pe.restaurante.dto.*;
import pe.restaurante.service.SalesService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/sales") public class SalesController{
 private final SalesService service;public SalesController(SalesService s){service=s;}
 @GetMapping public List<SaleResponse> list(){return service.list();}
 @PostMapping("/orders/{orderId}/checkout") @ResponseStatus(HttpStatus.CREATED) public SaleResponse checkout(@PathVariable Long orderId,@Valid @RequestBody CheckoutRequest r,Authentication a){return service.checkout(orderId,r,a.getName());}
 @PostMapping("/{id}/void") @PreAuthorize("hasRole('ADMIN')") public SaleResponse voidSale(@PathVariable Long id,@Valid @RequestBody VoidSaleRequest r,Authentication a){return service.voidSale(id,r,a.getName());}
}
