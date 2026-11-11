package pe.restaurante.service;
import pe.restaurante.dto.ReportResponse;
import pe.restaurante.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
@Service public class ReportService{
 private final RestaurantSaleRepository sales;private final OrderItemRepository items;
 public ReportService(RestaurantSaleRepository s,OrderItemRepository i){sales=s;items=i;}
 @Transactional(readOnly=true) public ReportResponse summarize(String period){var zone=ZoneId.of("America/Lima");var today=LocalDate.now(zone);LocalDate from=switch(period){case "DAILY"->today;case "WEEKLY"->today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));case "MONTHLY"->today.withDayOfMonth(1);default->throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Periodo inválido: DAILY, WEEKLY o MONTHLY");};var until=switch(period){case "DAILY"->from.plusDays(1);case "WEEKLY"->from.plusWeeks(1);default->from.plusMonths(1);};var fromInstant=from.atStartOfDay(zone).toInstant();var toInstant=until.atStartOfDay(zone).toInstant();var selected=sales.findAllByOrderByCreatedAtDesc().stream().filter(s->"COMPLETED".equals(s.getStatus())&&!s.getCreatedAt().isBefore(fromInstant)&&s.getCreatedAt().isBefore(toInstant)).toList();var revenue=selected.stream().map(s->s.getTotal()).reduce(BigDecimal.ZERO,BigDecimal::add);var rank=new HashMap<String,long[]>();var amount=new HashMap<String,BigDecimal>();for(var sale:selected)for(var item:items.findAllByOrderId(sale.getOrder().getId())){String name=item.getProduct().getName();rank.computeIfAbsent(name,k->new long[1])[0]+=item.getQuantity();amount.merge(name,item.getSubtotal(),BigDecimal::add);}var best=rank.entrySet().stream().map(e->new ReportResponse.ProductRow(e.getKey(),e.getValue()[0],amount.get(e.getKey()))).sorted(Comparator.comparingLong(ReportResponse.ProductRow::quantity).reversed()).limit(10).toList();var average=selected.isEmpty()?BigDecimal.ZERO:revenue.divide(BigDecimal.valueOf(selected.size()),2,java.math.RoundingMode.HALF_UP);return new ReportResponse(period,from,until.minusDays(1),selected.size(),revenue,average,best);}
}
