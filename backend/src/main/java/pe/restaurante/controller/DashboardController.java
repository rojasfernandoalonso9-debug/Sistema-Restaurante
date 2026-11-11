package pe.restaurante.controller;
import pe.restaurante.repository.BusinessRecordRepository;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.math.BigDecimal;
import java.time.*;
import java.time.temporal.ChronoUnit;
@RestController @RequestMapping("/api/dashboard") public class DashboardController {
 private final BusinessRecordRepository records;
 public DashboardController(BusinessRecordRepository records){this.records=records;}
 @GetMapping public Map<String,Object> dashboard(){var sales=records.findAllByTypeOrderByIdDesc("sales");var zone=ZoneId.of("America/Lima");var today=LocalDate.now(zone);var byDay=new ArrayList<Map<String,Object>>();var labels=List.of("Lun","Mar","Mié","Jue","Vie","Sáb","Dom");var totals=new BigDecimal[7];Arrays.fill(totals,BigDecimal.ZERO);BigDecimal todayTotal=BigDecimal.ZERO;BigDecimal weekTotal=BigDecimal.ZERO;for(var sale:sales){if(sale.getPrice()==null||sale.getCreatedAt()==null)continue;var date=sale.getCreatedAt().atZone(zone).toLocalDate();if(date.equals(today))todayTotal=todayTotal.add(sale.getPrice());long offset=ChronoUnit.DAYS.between(today.minusDays(6),date);if(offset>=0&&offset<7){int index=(int)offset;totals[index]=totals[index].add(sale.getPrice());weekTotal=weekTotal.add(sale.getPrice());}}for(int i=0;i<7;i++){int labelIndex=today.minusDays(6-i).getDayOfWeek().getValue()-1;byDay.add(Map.of("day",labels.get(labelIndex),"sales",totals[i]));}return Map.of("salesByDay",byDay,"todaySales",todayTotal,"weekTotal",weekTotal,"salesCount",sales.size());}
}
