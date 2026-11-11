package pe.restaurante.service;
import pe.restaurante.dto.*;
import pe.restaurante.entity.*;
import pe.restaurante.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.util.List;
@Service public class InventoryService{
 private final InventoryItemRepository items;private final InventoryMovementRepository movements;private final RestaurantUserRepository users;
 public InventoryService(InventoryItemRepository i,InventoryMovementRepository m,RestaurantUserRepository u){items=i;movements=m;users=u;}
 public List<InventoryItem> list(){return items.findAllByOrderByNameAsc();}
 @Transactional public InventoryItem create(InventoryRequest r){var i=new InventoryItem();apply(i,r);i.setQuantity(BigDecimal.ZERO);return items.save(i);}
 @Transactional public InventoryItem update(Long id,InventoryRequest r){var i=items.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));apply(i,r);return i;}
 @Transactional public void delete(Long id){var i=items.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));if(!movements.findAllByItemIdOrderByCreatedAtDesc(id).isEmpty())throw new ResponseStatusException(HttpStatus.CONFLICT,"El insumo tiene movimientos registrados; conserva el historial y desactívalo");items.delete(i);}
 @Transactional public MovementResponse move(Long id,MovementRequest r,String email){var item=items.findLockedById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Insumo no encontrado"));var user=users.findByEmailIgnoreCase(email).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED));if("OUT".equals(r.movementType())&&item.getQuantity().compareTo(r.quantity())<0)throw new ResponseStatusException(HttpStatus.CONFLICT,"Existencias insuficientes");item.setQuantity("IN".equals(r.movementType())?item.getQuantity().add(r.quantity()):item.getQuantity().subtract(r.quantity()));var m=new InventoryMovement();m.setItem(item);m.setUser(user);m.setMovementType(r.movementType());m.setQuantity(r.quantity());m.setNote(r.note());return MovementResponse.from(movements.save(m));}
 @Transactional(readOnly=true) public List<MovementResponse> history(Long id){if(!items.existsById(id))throw new ResponseStatusException(HttpStatus.NOT_FOUND);return movements.findAllByItemIdOrderByCreatedAtDesc(id).stream().map(MovementResponse::from).toList();}
 private void apply(InventoryItem i,InventoryRequest r){i.setName(r.name().trim());i.setUnit(r.unit().trim());i.setMinimumQuantity(r.minimumQuantity());}
}
