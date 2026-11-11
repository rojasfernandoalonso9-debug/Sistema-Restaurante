package pe.restaurante.dto;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
public record ReportResponse(String period,LocalDate from,LocalDate to,long completedSales,BigDecimal revenue,BigDecimal averageSale,List<ProductRow> bestSellingProducts){
 public record ProductRow(String name,long quantity,BigDecimal revenue){}
}
