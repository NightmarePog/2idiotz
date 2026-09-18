package cloud.twoidiotz.app;

import static org.junit.jupiter.api.Assertions.*;

import cloud.twoidiotz.app.v1.domain.station.StationModel;
import cloud.twoidiotz.app.v1.domain.station.StationRepository;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.*;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import tools.jackson.databind.json.JsonMapper;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StopApiIntegrationTest {
  @LocalServerPort int port;
  @Autowired StationRepository stations;
  private final JsonMapper mapper = JsonMapper.builder().build();
  private static final String INPUT =
      """
      {"name":"Cejl","wheelchair_accessible":true,"has_shelter":true,"has_ticket_machine":false}
      """;

  @Test
  void createsReadsReplacesAndDeletesStops() throws Exception {
    var created = request("POST", "/stops", INPUT);
    assertEquals(201, created.statusCode());
    var body = mapper.readTree(created.body());
    var id = body.get("id").asLong();
    try {
      assertEquals("/api/v1/stops/" + id, created.headers().firstValue("Location").orElseThrow());
      assertEquals(
          Set.of(
              "id",
              "name",
              "image_url",
              "wheelchair_accessible",
              "has_shelter",
              "has_ticket_machine"),
          new HashSet<>(body.propertyNames()));
      assertTrue(body.get("image_url").isNull());
      assertEquals("Cejl", body.get("name").asString());
      assertTrue(body.get("wheelchair_accessible").asBoolean());
      var stored = stations.findById(id).orElseThrow();
      assertNull(stored.getX());
      assertNull(stored.getY());
      assertEquals(body, mapper.readTree(request("GET", "/stops/" + id, null).body()));
      var list = request("GET", "/stops", null);
      assertEquals(200, list.statusCode());
      assertEquals("no-store", list.headers().firstValue("Cache-Control").orElseThrow());
      boolean found = false;
      for (var stop : mapper.readTree(list.body())) if (stop.get("id").asLong() == id) found = true;
      assertTrue(found);
      var update =
          request("PUT", "/stops/" + id, INPUT.replace("Cejl", "Updated").replace("true", "false"));
      assertEquals(200, update.statusCode());
      assertEquals("Updated", mapper.readTree(update.body()).get("name").asString());
      assertFalse(stations.findById(id).orElseThrow().isWheelchairAccessible());
      var deleted = request("DELETE", "/stops/" + id, null);
      assertEquals(204, deleted.statusCode());
      assertEquals("", deleted.body());
      assertFalse(stations.existsById(id));
      assertEquals(404, request("GET", "/stops/" + id, null).statusCode());
      assertEquals(404, request("PUT", "/stops/" + id, INPUT).statusCode());
      assertEquals(404, request("DELETE", "/stops/" + id, null).statusCode());
    } finally {
      stations.deleteById(id);
    }
  }

  @Test
  void replacementPreservesInternalFieldsAndClearsOmittedImage() throws Exception {
    var station = new StationModel();
    station.setName("Internal station");
    station.setX(new BigDecimal("12.3"));
    station.setY(new BigDecimal("45.6"));
    station.setTransfer(true);
    station.setHasBench(true);
    station.setHasDisplay(true);
    station.setImageUrl("https://example.com/old.png");
    var id = stations.saveAndFlush(station).getId();
    try {
      assertEquals(200, request("PUT", "/stops/" + id, INPUT).statusCode());
      var saved = stations.findById(id).orElseThrow();
      assertNull(saved.getImageUrl());
      assertEquals(0, station.getX().compareTo(saved.getX()));
      assertEquals(0, station.getY().compareTo(saved.getY()));
      assertTrue(saved.isTransfer());
      assertTrue(saved.isHasBench());
      assertTrue(saved.isHasDisplay());
      var explicit =
          INPUT.replace("\"name\":", "\"image_url\":\"https://example.com/new.png\",\"name\":");
      assertEquals(200, request("PUT", "/stops/" + id, explicit).statusCode());
      assertEquals(
          "https://example.com/new.png", stations.findById(id).orElseThrow().getImageUrl());
      assertEquals(
          200,
          request("PUT", "/stops/" + id, INPUT.replace("\"name\":", "\"image_url\":null,\"name\":"))
              .statusCode());
      for (var invalid :
          new String[] {
            "{}",
            INPUT.replace("\"Cejl\"", "\"\""),
            INPUT.replace("\"Cejl\"", "\"" + "x".repeat(256) + "\""),
            INPUT.replace("\"wheelchair_accessible\":true,", ""),
            INPUT.replace("\"has_shelter\":true,", ""),
            INPUT.replace(",\"has_ticket_machine\":false", ""),
            INPUT.replace("true", "null"),
            "{",
            "null"
          }) {
        assertEquals(400, request("POST", "/stops", invalid).statusCode(), invalid);
        assertEquals(400, request("PUT", "/stops/" + id, invalid).statusCode(), invalid);
      }
      assertEquals(400, request("GET", "/stops/not-an-id", null).statusCode());
      assertEquals("Cejl", stations.findById(id).orElseThrow().getName());
    } finally {
      stations.deleteById(id);
    }
  }

  @Test
  void validatesStrictInputsWithoutChangingStoredStops() throws Exception {
    var created = request("POST", "/stops", INPUT);
    var id = mapper.readTree(created.body()).get("id").asLong();
    try {
      var invalid = new java.util.ArrayList<String>();
      for (var field :
          new String[] {"name", "wheelchair_accessible", "has_shelter", "has_ticket_machine"}) {
        var missing = (tools.jackson.databind.node.ObjectNode) mapper.readTree(INPUT);
        missing.remove(field);
        invalid.add(missing.toString());
        for (var value : new String[] {"null", "42", "[]", "{}", "\"true\""}) {
          if (field.equals("name") && value.equals("\"true\"")) continue;
          var wrong = (tools.jackson.databind.node.ObjectNode) mapper.readTree(INPUT);
          wrong.set(field, mapper.readTree(value));
          invalid.add(wrong.toString());
        }
      }
      for (var image :
          new String[] {
            "",
            "images/test.png",
            "/image.png",
            "not a URL",
            "https://",
            "https://example.com/" + "x".repeat(236)
          }) {
        var input = (tools.jackson.databind.node.ObjectNode) mapper.readTree(INPUT);
        input.put("image_url", image);
        invalid.add(input.toString());
      }
      for (var extra : new String[] {"id", "unknown", "imageUrl"}) {
        var input = (tools.jackson.databind.node.ObjectNode) mapper.readTree(INPUT);
        input.put(extra, 1);
        invalid.add(input.toString());
      }
      invalid.add(INPUT.replace("\"name\":", "\"image_url\":false,\"name\":"));
      invalid.add(INPUT.replace("\"Cejl\"", "false"));
      invalid.add(INPUT.replace("\"Cejl\"", "\"\""));
      invalid.add(INPUT.replace("Cejl", "x".repeat(256)));
      invalid.addAll(java.util.List.of("null", "[]", "{", ""));
      for (var input : invalid) {
        assertError(400, request("POST", "/stops", input));
        assertError(400, request("PUT", "/stops/" + id, input));
      }
      assertEquals(
          mapper.readTree(created.body()),
          mapper.readTree(request("GET", "/stops/" + id, null).body()));
      for (var image :
          new String[] {
            "https://example.com/stops/cejl.jpg", "https://example.com/" + "x".repeat(235)
          }) {
        var input = (tools.jackson.databind.node.ObjectNode) mapper.readTree(INPUT);
        input.put("name", "x".repeat(255));
        input.put("image_url", image);
        var result = request("POST", "/stops", input.toString());
        assertEquals(201, result.statusCode(), result.body());
        var newId = mapper.readTree(result.body()).get("id").asLong();
        stations.deleteById(newId);
        assertEquals(200, request("PUT", "/stops/" + id, input.toString()).statusCode());
      }
    } finally {
      stations.deleteById(id);
    }
  }

  @Test
  void returnsContractErrorsForInvalidAndMissingIds() throws Exception {
    for (var method : new String[] {"GET", "PUT", "DELETE"}) {
      for (var id : new String[] {"0", "-1", "1.5", "abc", "9223372036854775808"}) {
        assertError(400, request(method, "/stops/" + id, method.equals("PUT") ? INPUT : null));
      }
      assertError(
          404, request(method, "/stops/9223372036854775807", method.equals("PUT") ? INPUT : null));
    }
  }

  @Test
  void seededImageUrlsUseThePublicProxyOrigin() throws Exception {
    assertSeededImageUrls("https", "transit.example.com:80", "443", "https://transit.example.com");
  }

  @Test
  void seededImageUrlsPreserveTheLocalDockerPort() throws Exception {
    assertSeededImageUrls("http", "localhost:8080", "8080", "http://localhost:8080");
  }

  private void assertSeededImageUrls(String scheme, String host, String publicPort, String origin)
      throws Exception {
    var response =
        HttpClient.newHttpClient()
            .send(
                HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api/v1/stops"))
                    .header("X-Forwarded-Proto", scheme)
                    .header("X-Forwarded-Host", host)
                    .header("X-Forwarded-Port", publicPort)
                    .build(),
                HttpResponse.BodyHandlers.ofString());
    assertEquals(200, response.statusCode());
    var stops = mapper.readTree(response.body());
    assertEquals(
        origin + "/api/v1/stops-images/turingTerminal.png",
        stops.get(0).get("image_url").asString());
    var quantum = stations.findById(15L).orElseThrow();
    assertEquals("Quantum Commons", quantum.getName());
    for (var stop : stops) {
      if (stop.get("id").asLong() == quantum.getId()) {
        assertEquals(
            origin + "/api/v1/stops-images/quantum%20Commons.png",
            stop.get("image_url").asString());
        return;
      }
    }
    fail("Quantum Commons missing from stop list");
  }

  private void assertError(int status, HttpResponse<String> response) throws Exception {
    assertEquals(status, response.statusCode(), response.body());
    assertTrue(
        response.headers().firstValue("Content-Type").orElse("").startsWith("application/json"));
    var body = mapper.readTree(response.body());
    assertEquals(Set.of("error"), new HashSet<>(body.propertyNames()));
    assertTrue(body.get("error").isString());
    assertFalse(body.get("error").asString().isEmpty());
  }

  private HttpResponse<String> request(String method, String path, String body) throws Exception {
    var builder = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api/v1" + path));
    if (body != null) builder.header("Content-Type", "application/json");
    return HttpClient.newHttpClient()
        .send(
            builder
                .method(
                    method,
                    body == null
                        ? HttpRequest.BodyPublishers.noBody()
                        : HttpRequest.BodyPublishers.ofString(body))
                .build(),
            HttpResponse.BodyHandlers.ofString());
  }
}
