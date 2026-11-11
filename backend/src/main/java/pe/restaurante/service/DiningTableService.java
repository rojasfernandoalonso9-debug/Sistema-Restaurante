package pe.restaurante.service;
import pe.restaurante.dto.TableRequest;
import pe.restaurante.entity.DiningTable;
import pe.restaurante.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
@Service public class DiningTableService{
 private final DiningTableRepository tables;private final RestaurantOrderRepository orders;
 public DiningTableService(DiningTableRepository t,RestaurantOrderRepository o){tables=t;orders=o;}
 public List<DiningTable> list(){return tables.findAllByOrderByTableNumberAsc();}
 @Transactional public DiningTable create(TableRequest r){var t=new DiningTable();apply(t,r);return tables.save(t);}
 @Transactional public DiningTable update(Long id,TableRequest r){var t=tables.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));if("OCCUPIED".equals(t.getStatus())&&r.status()!=null&&"AVAILABLE".equals(r.status()))throw new ResponseStatusException(HttpStatus.CONFLICT,"Cierra o cancela el pedido antes de liberar la mesa");apply(t,r);return t;}
 @Transactional public void delete(Long id){var t=tables.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));if("OCCUPIED".equals(t.getStatus())||orders.existsByTableId(id))throw new ResponseStatusException(HttpStatus.CONFLICT,"La mesa tiene pedidos asociados o está ocupada");tables.delete(t);}
 private void apply(DiningTable t,TableRequest r){t.setTableNumber(r.tableNumber().trim());t.setCapacity(r.capacity());if(r.status()!=null&&!r.status().isBlank())t.setStatus(r.status());}
}
