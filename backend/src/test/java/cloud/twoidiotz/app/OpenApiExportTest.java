package cloud.twoidiotz.app;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ObjectNode;

@Tag("openapi")
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {"springdoc.api-docs.enabled=true", "springdoc.writer-with-order-by-keys=true"})
class OpenApiExportTest {
  @LocalServerPort int port;

  @Value("${server.servlet.context-path}")
  String contextPath;

  @Test
  void exportsContractAndLiveResponse() throws Exception {
    var mapper = JsonMapper.builder().build();
    var client = HttpClient.newHttpClient();
    var origin = "http://localhost:" + port + contextPath;
    var docs =
        client.send(
            HttpRequest.newBuilder(URI.create(origin + "/v3/api-docs")).build(),
            HttpResponse.BodyHandlers.ofString());
    assertEquals(200, docs.statusCode());
    var contract = (ObjectNode) mapper.readTree(docs.body());
    // Keep the public prefix, but never generate a client tied to a random test port.
    contract.putArray("servers").addObject().put("url", contextPath);
    assertEquals("getHealth", contract.at("/paths/~1health/get/operationId").asString());
    assertEquals(
        "ok",
        contract.at("/components/schemas/HealthResponse/properties/status/enum/0").asString());
    assertEquals("status", contract.at("/components/schemas/HealthResponse/required/0").asString());

    var health =
        client.send(
            HttpRequest.newBuilder(URI.create(origin + "/health")).build(),
            HttpResponse.BodyHandlers.ofString());
    assertEquals(200, health.statusCode());
    var output = Path.of(System.getProperty("openapi.output"));
    Files.createDirectories(output);
    Files.writeString(
        output.resolve("openapi.json"),
        mapper.writerWithDefaultPrettyPrinter().writeValueAsString(contract) + "\n");
    Files.writeString(output.resolve("health.json"), health.body());
  }
}
