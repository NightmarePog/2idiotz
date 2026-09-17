package cloud.twoidiotz.infrastructure;

import cloud.twoidiotz.domain.health.DatabaseProbe;
import cloud.twoidiotz.infrastructure.health.JdbcDatabaseProbe;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration(proxyBeanMethods = false)
public class DatabaseConfiguration {
  @Bean
  DatabaseProbe databaseProbe(JdbcTemplate jdbc) {
    return new JdbcDatabaseProbe(jdbc);
  }
}
