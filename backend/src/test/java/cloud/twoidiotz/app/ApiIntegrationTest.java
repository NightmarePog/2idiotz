package cloud.twoidiotz.app;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiIntegrationTest {
  @LocalServerPort int port;

  @Test
  void servesHealthContract() throws Exception {
    var client = HttpClient.newHttpClient();
    var health =
        client.send(
            HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api/v1/health"))
                .build(),
            HttpResponse.BodyHandlers.ofString());
    assertEquals(200, health.statusCode());
    assertEquals("{\"status\":\"ok\"}", health.body());
    assertEquals("no-store", health.headers().firstValue("Cache-Control").orElseThrow());
  }
}
