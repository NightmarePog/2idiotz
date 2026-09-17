package cloud.twoidiotz.app;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.postgresql.PostgreSQLContainer;

@Tag("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiIntegrationTest {
  static final PostgreSQLContainer DATABASE = new PostgreSQLContainer("postgres:17-alpine");

  static {
    DATABASE.start();
  }

  @DynamicPropertySource
  static void databaseProperties(DynamicPropertyRegistry properties) {
    properties.add("spring.datasource.url", DATABASE::getJdbcUrl);
    properties.add("spring.datasource.username", DATABASE::getUsername);
    properties.add("spring.datasource.password", DATABASE::getPassword);
  }

  @LocalServerPort int port;

  @Test
  void servesTheExistingApiContractWithRealPostgres() throws Exception {
    var client = HttpClient.newHttpClient();
    var health =
        client.send(
            HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api/health")).build(),
            HttpResponse.BodyHandlers.ofString());
    assertEquals(200, health.statusCode());
    assertEquals("{\"status\":\"UP\",\"database\":\"UP\"}", health.body());
    assertEquals("no-store", health.headers().firstValue("Cache-Control").orElseThrow());
    var hello =
        client.send(
            HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api/hello")).build(),
            HttpResponse.BodyHandlers.ofString());
    assertEquals(200, hello.statusCode());
    assertEquals("{\"message\":\"Hello from Spring Boot!\"}", hello.body());
  }
}
