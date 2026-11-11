package pe.restaurante.dto;
import pe.restaurante.entity.Employee;
public record EmployeeResponse(Long id,String fullName,String jobTitle,String phone,Long userId,String email,String employmentStatus){
 public static EmployeeResponse from(Employee e){return new EmployeeResponse(e.getId(),e.getFullName(),e.getJobTitle(),e.getPhone(),e.getUser()==null?null:e.getUser().getId(),e.getUser()==null?null:e.getUser().getEmail(),e.getEmploymentStatus());}
}
