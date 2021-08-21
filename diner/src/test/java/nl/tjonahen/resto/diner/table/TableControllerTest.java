package nl.tjonahen.resto.diner.table;

import nl.tjonahen.resto.diner.order.model.Table;
import nl.tjonahen.resto.diner.persistence.TableRepository;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TableControllerTest {

    @Mock
    private TableRepository tableRepositoryMock;
    
    @InjectMocks
    private TableController sut;
    
    
    @Test
    public void testReserveTable() {
        final Table result = sut.reserveTable();
        
        assertNotNull(result);
        
        verify(tableRepositoryMock).save(any(Table.class));
    }
    
}
