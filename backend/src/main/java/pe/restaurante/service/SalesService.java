package pe.restaurante.service;
import pe.restaurante.dto.*;
import pe.restaurante.entity.RestaurantSale;
import pe.restaurante.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
@Service public class SalesService{
 private final RestaurantSaleRepository sales;private final RestaurantOrderRepository orders;private final RestaurantUserRepository users;
 public SalesService(RestaurantSaleRepository s,RestaurantOrderRepository o,RestaurantUserRepository u){sales=s;orders=o;users=u;}
 @Transactional(readOnly=true) public List<SaleResponse> list(){return sales.findAllByOrderByCreatedAtDesc().stream().map(SaleResponse::from).toList();}
 @Transactional public SaleResponse checkout(Long orderId,CheckoutRequest request,String email){var order=orders.findLockedById(orderId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Pedido no encontrado"));if(sales.findByOrderId(orderId).isPresent())throw new ResponseStatusException(HttpStatus.CONFLICT,"Este pedido ya fue cobrado");if(!"DELIVERED".equals(order.getStatus()))throw new ResponseStatusException(HttpStatus.CONFLICT,"Entrega el pedido antes de cobrarlo");var cashier=users.findByEmailIgnoreCase(email).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED));var sale=new RestaurantSale();sale.setOrder(order);sale.setUser(cashier);sale.setTotal(order.getTotal());sale.setPaymentMethod(request.paymentMethod());var saved=sales.save(sale);if(order.getTable()!=null)order.getTable().setStatus("AVAILABLE");return SaleResponse.from(saved);}
 @Transactional public SaleResponse voidSale(Long id,VoidSaleRequest request,String email){var sale=sales.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Venta no encontrada"));if(!"COMPLETED".equals(sale.getStatus()))throw new ResponseStatusException(HttpStatus.CONFLICT,"La venta ya fue anulada");var admin=users.findByEmailIgnoreCase(email).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED));sale.setStatus("VOIDED");sale.setVoidedBy(admin);sale.setVoidReason(request.reason().trim());sale.setVoidedAt(Instant.now());return SaleResponse.from(sale);}
}
