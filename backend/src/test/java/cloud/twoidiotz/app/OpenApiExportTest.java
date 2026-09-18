package cloud.twoidiotz.app;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ObjectNode;

@Tag("openapi")
@ActiveProfiles("test")
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
    assertEquals("getTeam", contract.at("/paths/~1team/get/operationId").asString());
    var team =
        client.send(
            HttpRequest.newBuilder(URI.create(origin + "/team")).build(),
            HttpResponse.BodyHandlers.ofString());
    assertEquals(200, team.statusCode());
    assertFalse(mapper.readTree(team.body()).get("members").isEmpty());
    assertEquals("3.1.0", contract.get("openapi").asString());
    assertEquals("getStops", contract.at("/paths/~1stops/get/operationId").asString());
    assertEquals("createStop", contract.at("/paths/~1stops/post/operationId").asString());
    assertEquals("getStop", contract.at("/paths/~1stops~1{id}/get/operationId").asString());
    assertEquals("updateStop", contract.at("/paths/~1stops~1{id}/put/operationId").asString());
    assertEquals("deleteStop", contract.at("/paths/~1stops~1{id}/delete/operationId").asString());
    assertTrue(contract.at("/paths/~1stops/post/responses").has("201"));
    assertTrue(contract.at("/paths/~1stops~1{id}/delete/responses").has("204"));
    assertEquals("1.1.0", contract.at("/info/version").asString());
    for (var method : new String[] {"get", "put", "delete"}) {
      var operation = contract.at("/paths/~1stops~1{id}/" + method);
      assertEquals(1, operation.at("/parameters/0/schema/minimum").asInt());
      for (var status : new String[] {"400", "404"}) {
        assertEquals(
            "#/components/schemas/Error",
            operation
                .at("/responses/" + status + "/content/application~1json/schema/$ref")
                .asString());
      }
    }
    assertEquals(
        "#/components/schemas/Error",
        contract
            .at("/paths/~1stops/post/responses/400/content/application~1json/schema/$ref")
            .asString());
    for (var schema : new String[] {"Stop", "StopInput", "Error"}) {
      assertFalse(
          contract.at("/components/schemas/" + schema + "/additionalProperties").asBoolean(true));
    }
    assertEquals(1, contract.at("/components/schemas/StopInput/properties/name/minLength").asInt());
    var stopSchema = contract.at("/components/schemas/Stop");
    assertEquals("int64", stopSchema.at("/properties/id/format").asString());
    assertTrue(stopSchema.at("/properties/id/readOnly").asBoolean());
    assertEquals(
        Set.of(
            "id",
            "name",
            "image_url",
            "wheelchair_accessible",
            "has_shelter",
            "has_ticket_machine"),
        stopSchema
            .get("required")
            .valueStream()
            .map(node -> node.asString())
            .collect(Collectors.toSet()));
    for (var schemaName : new String[] {"Stop", "StopInput"}) {
      var properties = contract.at("/components/schemas/" + schemaName + "/properties");
      assertTrue(properties.has("image_url"));
      assertTrue(properties.has("wheelchair_accessible"));
      assertFalse(properties.has("imageUrl"));
      assertEquals("uri", properties.get("image_url").get("format").asString());
    }
    var stops =
        client.send(
            HttpRequest.newBuilder(URI.create(origin + "/stops")).build(),
            HttpResponse.BodyHandlers.ofString());
    assertEquals(200, stops.statusCode());
    assertTrue(mapper.readTree(stops.body()).isArray());
    var output = Path.of(System.getProperty("openapi.output"));
    Files.createDirectories(output);
    Files.writeString(
        output.resolve("openapi.json"),
        mapper.writerWithDefaultPrettyPrinter().writeValueAsString(contract) + "\n");
    Files.writeString(output.resolve("health.json"), health.body());
    Files.writeString(output.resolve("team.json"), team.body());
    Files.writeString(output.resolve("stops.json"), stops.body());
  }
}
