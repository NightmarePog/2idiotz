package cloud.twoidiotz.app.config;

import cloud.twoidiotz.domain.health.DatabaseProbe;
import cloud.twoidiotz.domain.health.HealthService;
import cloud.twoidiotz.infrastructure.DatabaseConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration(proxyBeanMethods = false)
@Import(DatabaseConfiguration.class)
public class ApplicationConfiguration {
  @Bean
  HealthService healthService(DatabaseProbe database) {
    return new HealthService(database);
  }
}
