package pe.restaurante.service;
import pe.restaurante.dto.*;
import pe.restaurante.entity.Employee;
import pe.restaurante.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
@Service public class EmployeeService{
 private final EmployeeRepository employees;private final RestaurantUserRepository users;
 public EmployeeService(EmployeeRepository e,RestaurantUserRepository u){employees=e;users=u;}
 @Transactional(readOnly=true) public List<EmployeeResponse> list(){return employees.findAllByOrderByFullNameAsc().stream().map(EmployeeResponse::from).toList();}
 @Transactional public EmployeeResponse create(EmployeeRequest r){if(r.userId()!=null&&employees.existsByUserId(r.userId()))throw new ResponseStatusException(HttpStatus.CONFLICT,"La cuenta ya está asociada a un empleado");var e=new Employee();apply(e,r);return EmployeeResponse.from(employees.save(e));}
 @Transactional public EmployeeResponse update(Long id,EmployeeRequest r){var e=employees.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));if(r.userId()!=null&&employees.existsByUserIdAndIdNot(r.userId(),id))throw new ResponseStatusException(HttpStatus.CONFLICT,"La cuenta ya está asociada a otro empleado");apply(e,r);return EmployeeResponse.from(e);}
 @Transactional public void delete(Long id){employees.delete(employees.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND)));}
 private void apply(Employee e,EmployeeRequest r){e.setFullName(r.fullName().trim());e.setJobTitle(r.jobTitle().trim());e.setPhone(r.phone());e.setEmploymentStatus(r.employmentStatus()==null?"ACTIVE":r.employmentStatus());e.setUser(r.userId()==null?null:users.findById(r.userId()).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST,"Cuenta de usuario inexistente")));}
}
