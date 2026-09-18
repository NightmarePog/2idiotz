package cloud.twoidiotz.app;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
    info =
        @Info(
            title = "Think different Transit API",
            version = "1.1.0",
            description = "API for managing public transport stops in Think different Academy."),
    servers = @Server(url = "/api/v1"))
@SpringBootApplication
public class Application {
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
