package cloud.twoidiotz.app.v1.domain.station;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice(assignableTypes = StationController.class)
public class StationExceptionHandler {
  @ExceptionHandler(StationNotFoundException.class)
  public ResponseEntity<StopError> notFound() {
    return ResponseEntity.status(404).body(new StopError("Stop not found"));
  }

  @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentNotValidException.class})
  public ResponseEntity<StopError> invalidInput() {
    return ResponseEntity.badRequest().body(new StopError("Invalid input data"));
  }

  @ExceptionHandler({
    MethodArgumentTypeMismatchException.class,
    HandlerMethodValidationException.class
  })
  public ResponseEntity<StopError> invalidId() {
    return ResponseEntity.badRequest().body(new StopError("Invalid stop ID"));
  }
}
