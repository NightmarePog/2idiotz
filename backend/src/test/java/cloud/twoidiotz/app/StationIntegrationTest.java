package cloud.twoidiotz.app;

import static org.junit.jupiter.api.Assertions.*;

import cloud.twoidiotz.app.v1.domain.station.StationModel;
import cloud.twoidiotz.app.v1.domain.station.StationRepository;
import jakarta.persistence.EntityManager;
import jakarta.validation.Validator;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import org.apache.commons.csv.CSVFormat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriUtils;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class StationIntegrationTest {
  @LocalServerPort int port;
  @Autowired StationRepository stations;
  @Autowired EntityManager entities;
  @Autowired Validator validator;

  @Test
  void seedsEveryStationFromCsv() throws Exception {
    try (var reader =
            new InputStreamReader(
                getClass().getResourceAsStream("/seed/v5/stops.csv"), StandardCharsets.UTF_8);
        var records =
            CSVFormat.RFC4180.builder().setHeader().setSkipHeaderRecord(true).get().parse(reader)) {
      var rows = records.getRecords();
      assertEquals(15, rows.size());
      for (var row : rows) {
        var station = stations.findById(Long.parseLong(row.get("id"))).orElseThrow();
        assertEquals(row.get("name"), station.getName());
        assertEquals(Boolean.parseBoolean(row.get("is_transfer")), station.isTransfer());
        assertEquals(0, new BigDecimal(row.get("x")).compareTo(station.getX()));
        assertEquals(0, new BigDecimal(row.get("y")).compareTo(station.getY()));
        assertEquals(
            Boolean.parseBoolean(row.get("wheelchair_accessible")),
            station.isWheelchairAccessible());
        assertEquals(Boolean.parseBoolean(row.get("has_shelter")), station.isHasShelter());
        assertEquals(Boolean.parseBoolean(row.get("has_bench")), station.isHasBench());
        assertEquals(
            Boolean.parseBoolean(row.get("has_ticket_machine")), station.isHasTicketMachine());
        assertEquals(Boolean.parseBoolean(row.get("has_display")), station.isHasDisplay());
        assertTrue(validator.validate(station).isEmpty());
        assertEquals(row.get("image_url"), station.getImageUrl());
        assertTrue(station.getImageUrl().startsWith("/api/v1/stops-images/"));
        var response =
            HttpClient.newHttpClient()
                .send(
                    HttpRequest.newBuilder(
                            URI.create("http://localhost:" + port + station.getImageUrl()))
                        .build(),
                    HttpResponse.BodyHandlers.ofByteArray());
        assertEquals(200, response.statusCode());
        assertEquals("image/png", response.headers().firstValue("Content-Type").orElseThrow());
        var filename =
            UriUtils.decode(
                station.getImageUrl().substring("/api/v1/stops-images/".length()),
                StandardCharsets.UTF_8);
        try (var image = new ClassPathResource("seed/stopsImages/" + filename).getInputStream()) {
          assertArrayEquals(image.readAllBytes(), response.body());
        }
      }
      assertEquals(
          "/api/v1/stops-images/quantum%20Commons.png",
          stations.findById(15L).orElseThrow().getImageUrl());
    }
  }

  @Test
  void persistsEveryFieldAndGeneratesUniqueIds() {
    var station = station();
    station.setImageUrl("https://example.com/station.jpg");
    station.setTransfer(true);
    station.setWheelchairAccessible(true);
    station.setHasShelter(true);
    station.setHasBench(false);
    station.setHasTicketMachine(true);
    station.setHasDisplay(false);
    var id = stations.saveAndFlush(station).getId();
    var second = station();
    second.setName("N".repeat(255));
    var secondId = stations.saveAndFlush(second).getId();
    assertNotNull(id);
    assertTrue(id > 15);
    assertNotEquals(id, secondId);
    entities.clear();

    var loaded = stations.findById(id).orElseThrow();
    assertEquals("Náměstí Míru", loaded.getName());
    assertEquals("https://example.com/station.jpg", loaded.getImageUrl());
    assertEquals(0, new BigDecimal("-12.345678").compareTo(loaded.getX()));
    assertEquals(0, new BigDecimal("98.765432").compareTo(loaded.getY()));
    assertTrue(loaded.isTransfer());
    assertTrue(loaded.isWheelchairAccessible());
    assertTrue(loaded.isHasShelter());
    assertFalse(loaded.isHasBench());
    assertTrue(loaded.isHasTicketMachine());
    assertFalse(loaded.isHasDisplay());
    var defaults = stations.findById(secondId).orElseThrow();
    assertNull(defaults.getImageUrl());
    assertFalse(defaults.isTransfer());
    assertFalse(defaults.isWheelchairAccessible());
    assertFalse(defaults.isHasShelter());
    assertFalse(defaults.isHasBench());
    assertFalse(defaults.isHasTicketMachine());
    assertFalse(defaults.isHasDisplay());
  }

  @Test
  void validatesRequiredFieldsAndLengths() {
    var station = station();
    assertTrue(validator.validate(station).isEmpty());
    for (String invalidName : new String[] {null, "", "   ", "n".repeat(256)}) {
      station.setName(invalidName);
      assertFalse(validator.validateProperty(station, "name").isEmpty());
    }
    station.setX(null);
    station.setY(null);
    assertTrue(validator.validateProperty(station, "x").isEmpty());
    assertTrue(validator.validateProperty(station, "y").isEmpty());
    var prefix = "https://example.com/";
    station.setImageUrl(prefix + "x".repeat(255 - prefix.length()));
    assertTrue(validator.validateProperty(station, "imageUrl").isEmpty());
    station.setImageUrl(station.getImageUrl() + "x");
    assertFalse(validator.validateProperty(station, "imageUrl").isEmpty());
    station.setImageUrl("images/station photo.webp");
    assertTrue(validator.validateProperty(station, "imageUrl").isEmpty());
  }

  private StationModel station() {
    var station = new StationModel();
    station.setName("Náměstí Míru");
    station.setX(new BigDecimal("-12.345678"));
    station.setY(new BigDecimal("98.765432"));
    return station;
  }
}
