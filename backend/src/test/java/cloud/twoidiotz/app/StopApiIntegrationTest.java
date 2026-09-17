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
            INPUT.replace("\"Cejl\"", "\" \""),
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
  void acceptsImageStringsWithoutUrlOrFilenameRestrictions() throws Exception {
    for (var image :
        new String[] {
          "images/station photo.webp", "/uploads/cejl.svg", "not a URL", "", "x".repeat(255)
        }) {
      var input = (tools.jackson.databind.node.ObjectNode) mapper.readTree(INPUT);
      input.put("image_url", image);
      var created = request("POST", "/stops", input.toString());
      assertEquals(201, created.statusCode(), image);
      var body = mapper.readTree(created.body());
      var id = body.get("id").asLong();
      try {
        assertEquals(image, body.get("image_url").asString());
        input.put("image_url", "another image.avif");
        assertEquals(200, request("PUT", "/stops/" + id, input.toString()).statusCode());
        assertEquals("another image.avif", stations.findById(id).orElseThrow().getImageUrl());
        input.put("image_url", "x".repeat(256));
        assertEquals(400, request("PUT", "/stops/" + id, input.toString()).statusCode());
      } finally {
        stations.deleteById(id);
      }
    }
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
