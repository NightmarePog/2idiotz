package cloud.twoidiotz.app.greeting;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {
  @GetMapping("/api/hello")
  public GreetingResponse hello() {
    return new GreetingResponse("Hello from Spring Boot!");
  }
}
