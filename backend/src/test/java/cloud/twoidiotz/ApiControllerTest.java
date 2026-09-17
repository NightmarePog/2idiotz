package cloud.twoidiotz;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ApiControllerTest {
    @Test
    void reportsDatabaseFailureAndRecoveryWithoutLeakingDetails() {
        JdbcTemplate jdbc = mock(JdbcTemplate.class);
        when(jdbc.queryForObject("SELECT 1", Integer.class))
                .thenThrow(new DataAccessResourceFailureException("private connection details"))
                .thenReturn(1);
        ApiController api = new ApiController(jdbc);
        var unavailable = api.health();
        assertEquals(503, unavailable.getStatusCode().value());
        assertEquals(new ApiController.Health("DOWN", "DOWN"), unavailable.getBody());
        assertEquals("no-store", unavailable.getHeaders().getFirst("Cache-Control"));
        assertEquals(200, api.health().getStatusCode().value());
        assertEquals("Hello from Spring Boot!", api.hello().message());
    }
}
