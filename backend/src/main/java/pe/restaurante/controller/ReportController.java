package pe.restaurante.controller;
import pe.restaurante.dto.ReportResponse;
import pe.restaurante.service.ReportService;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/reports/summary") public class ReportController{
 private final ReportService service;public ReportController(ReportService s){service=s;}
 @GetMapping public ReportResponse summary(@RequestParam(defaultValue="DAILY") String period){return service.summarize(period.toUpperCase());}
}
