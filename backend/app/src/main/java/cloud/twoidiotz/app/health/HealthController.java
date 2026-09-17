package cloud.twoidiotz.app.health;

import cloud.twoidiotz.domain.health.HealthService;
import cloud.twoidiotz.domain.health.HealthStatus;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
  private final HealthService health;

  public HealthController(HealthService health) {
    this.health = health;
  }

  @GetMapping("/api/health")
  public ResponseEntity<HealthResponse> health() {
    HealthStatus status = health.check();
    return ResponseEntity.status(status == HealthStatus.UP ? 200 : 503)
        .cacheControl(CacheControl.noStore())
        .body(new HealthResponse(status, status));
  }
}
