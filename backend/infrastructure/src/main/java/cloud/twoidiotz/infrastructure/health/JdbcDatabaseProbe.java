package cloud.twoidiotz.infrastructure.health;

import cloud.twoidiotz.domain.health.DatabaseProbe;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public final class JdbcDatabaseProbe implements DatabaseProbe {
  private final JdbcTemplate jdbc;

  public JdbcDatabaseProbe(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  @Override
  public boolean isAvailable() {
    try {
      return Integer.valueOf(1).equals(jdbc.queryForObject("SELECT 1", Integer.class));
    } catch (DataAccessException exception) {
      return false;
    }
  }
}
