package pe.restaurante.service;

import org.junit.jupiter.api.Test;
import pe.restaurante.dto.RecordRequest;
import pe.restaurante.entity.BusinessRecord;
import pe.restaurante.repository.BusinessRecordRepository;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BusinessRecordServiceTest {
    private final BusinessRecordRepository repository=mock(BusinessRecordRepository.class);
    private final BusinessRecordService service=new BusinessRecordService(repository);
    @Test void createsRecordWithNormalizedNameAndModuleType(){
        when(repository.save(any())).thenAnswer(call->call.getArgument(0));
        var saved=service.create("products",new RecordRequest("  Café  ","Bebida",null,new BigDecimal("8.50"),null,null,null,null,null));
        assertEquals("products",saved.getType());assertEquals("Café",saved.getName());assertEquals(new BigDecimal("8.50"),saved.getPrice());
    }
    @Test void updateRejectsIdsFromOtherModules(){
        when(repository.findByIdAndType(4L,"tables")).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class,()->service.update("tables",4L,new RecordRequest("Mesa 4",null,null,null,null,null,null,4,null)));
    }
    @Test void deleteOnlyRemovesMatchingModuleRecord(){
        var record=new BusinessRecord();record.setType("inventory");record.setName("Harina");
        when(repository.findByIdAndType(2L,"inventory")).thenReturn(Optional.of(record));service.delete("inventory",2L);verify(repository).delete(record);
    }
}
