package cloud.twoidiotz.app.v1.domain.health;

import cloud.twoidiotz.app.api.ApiGet;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
  @ApiGet("/health")
  public ResponseEntity<HealthResponse> getHealth() {
    return ResponseEntity.ok()
        .cacheControl(CacheControl.noStore())
        .body(new HealthResponse(HealthResponse.Status.OK));
  }
}
