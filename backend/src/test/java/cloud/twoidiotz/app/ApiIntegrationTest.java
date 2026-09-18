package cloud.twoidiotz.app;

import static java.net.http.HttpClient.newHttpClient;
import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiIntegrationTest {
  @LocalServerPort int port;

  @Test
  void servesHealthContract() throws Exception {
    var client = newHttpClient();
    var health =
        client.send(
            HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api/v1/health"))
                .build(),
            HttpResponse.BodyHandlers.ofString());
    assertEquals(200, health.statusCode());
    assertEquals("{\"status\":\"ok\"}", health.body());
    assertEquals("no-store", health.headers().firstValue("Cache-Control").orElseThrow());
  }

  @Test
  void servesFrontendAssetsThroughBackend() throws Exception {
    var client = newHttpClient();
    for (var asset :
        new String[] {
          "assets/brand/tda-logo.svg",
          "assets/fonts/dosis-variable.ttf",
          "stops-images/turingTerminal.png"
        }) {
      var response =
          client.send(
              HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api/v1/" + asset))
                  .build(),
              HttpResponse.BodyHandlers.ofByteArray());
      assertEquals(200, response.statusCode(), asset);
      assertTrue(response.body().length > 0, asset);
    }
  }
}
