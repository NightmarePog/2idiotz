package cloud.twoidiotz.app.v1.domain.health;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record HealthResponse(@NotNull Status status) {
  public enum Status {
    @JsonProperty("ok")
    OK
  }
}
