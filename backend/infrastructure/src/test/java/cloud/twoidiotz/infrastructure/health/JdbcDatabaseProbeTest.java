package cloud.twoidiotz.infrastructure.health;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.core.JdbcTemplate;

class JdbcDatabaseProbeTest {
  @Test
  void translatesDatabaseFailureAndChecksAgainOnRecovery() {
    var jdbc = mock(JdbcTemplate.class);
    when(jdbc.queryForObject("SELECT 1", Integer.class))
        .thenThrow(new DataAccessResourceFailureException("private connection details"))
        .thenReturn(1);
    var probe = new JdbcDatabaseProbe(jdbc);
    assertFalse(probe.isAvailable());
    assertTrue(probe.isAvailable());
  }
}
