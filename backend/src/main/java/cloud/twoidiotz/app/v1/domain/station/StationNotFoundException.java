package cloud.twoidiotz.app.v1.domain.station;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Stop not found")
public class StationNotFoundException extends RuntimeException {
  public StationNotFoundException(long id) {
    super("Stop not found: " + id);
  }
}
