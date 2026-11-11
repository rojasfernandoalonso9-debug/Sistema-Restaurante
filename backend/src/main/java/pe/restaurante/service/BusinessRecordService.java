package pe.restaurante.service;
import pe.restaurante.dto.RecordRequest;
import pe.restaurante.entity.BusinessRecord;
import pe.restaurante.repository.BusinessRecordRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
@Service
public class BusinessRecordService {
    private final BusinessRecordRepository repository;
    public BusinessRecordService(BusinessRecordRepository repository){this.repository=repository;}
    public List<BusinessRecord> list(String type){return repository.findAllByTypeOrderByIdDesc(type);}
    @Transactional public BusinessRecord create(String type,RecordRequest request){BusinessRecord record=new BusinessRecord();record.setType(type);apply(record,request);return repository.save(record);}
    @Transactional public BusinessRecord update(String type,Long id,RecordRequest request){BusinessRecord record=repository.findByIdAndType(id,type).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Registro no encontrado"));apply(record,request);return record;}
    @Transactional public void delete(String type,Long id){BusinessRecord record=repository.findByIdAndType(id,type).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Registro no encontrado"));repository.delete(record);}
    private void apply(BusinessRecord record,RecordRequest request){record.setName(request.name().trim());record.setDescription(request.description());record.setStatus(request.status());record.setPrice(request.price());record.setStock(request.stock());record.setEmail(request.email());record.setRole(request.role());record.setCapacity(request.capacity());record.setTableName(request.tableName());}
}
