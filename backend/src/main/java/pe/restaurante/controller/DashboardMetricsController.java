package pe.restaurante.controller;
import pe.restaurante.repository.*;
import pe.restaurante.service.ReportService;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import org.springframework.transaction.annotation.Transactional;
@RestController @RequestMapping("/api/dashboard/metrics") public class DashboardMetricsController{
 private final RestaurantSaleRepository sales;private final DiningTableRepository tables;private final RestaurantOrderRepository orders;private final RestaurantProductRepository products;private final ReportService reports;
 public DashboardMetricsController(RestaurantSaleRepository s,DiningTableRepository t,RestaurantOrderRepository o,RestaurantProductRepository p,ReportService r){sales=s;tables=t;orders=o;products=p;reports=r;}
 @GetMapping @Transactional(readOnly=true) public Map<String,Object> metrics(){var zone=ZoneId.of("America/Lima");var today=LocalDate.now(zone);var rows=sales.findAllByOrderByCreatedAtDesc();var days=new ArrayList<Map<String,Object>>();var labels=List.of("Lun","Mar","Mié","Jue","Vie","Sáb","Dom");var totals=new BigDecimal[7];Arrays.fill(totals,BigDecimal.ZERO);BigDecimal todayTotal=BigDecimal.ZERO,weekTotal=BigDecimal.ZERO;for(var sale:rows){if(!"COMPLETED".equals(sale.getStatus())||sale.getCreatedAt()==null)continue;var date=sale.getCreatedAt().atZone(zone).toLocalDate();if(date.equals(today))todayTotal=todayTotal.add(sale.getTotal());long offset=ChronoUnit.DAYS.between(today.minusDays(6),date);if(offset>=0&&offset<7){int index=(int)offset;totals[index]=totals[index].add(sale.getTotal());weekTotal=weekTotal.add(sale.getTotal());}}for(int i=0;i<7;i++){int labelIndex=today.minusDays(6-i).getDayOfWeek().getValue()-1;days.add(Map.of("day",labels.get(labelIndex),"sales",totals[i]));}var recent=orders.findAllByOrderByCreatedAtDesc().stream().limit(5).map(o->Map.<String,Object>of("id",o.getId(),"tableName",o.getTable()==null?"Para llevar":o.getTable().getTableNumber(),"status",o.getStatus(),"total",o.getTotal(),"createdAt",o.getCreatedAt())).toList();var best=reports.summarize("WEEKLY").bestSellingProducts();long activeOrders=orders.countByStatusIn(List.of("PENDING","PREPARING","READY"));return Map.of("salesByDay",days,"todaySales",todayTotal,"weekTotal",weekTotal,"salesCount",rows.size(),"pendingOrders",activeOrders,"occupiedTables",tables.countByStatus("OCCUPIED"),"activeProducts",products.countByAvailableTrue(),"recentOrders",recent,"bestSellingProducts",best);}
}
