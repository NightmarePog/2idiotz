package cloud.twoidiotz.domain.health;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;

class HealthServiceTest {
  @Test
  void reflectsOutageAndRecovery() {
    var available = new AtomicBoolean(false);
    var service = new HealthService(available::get);
    assertEquals(HealthStatus.DOWN, service.check());
    available.set(true);
    assertEquals(HealthStatus.UP, service.check());
  }
}
