package cloud.twoidiotz.app;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.UUID;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import seed.V5__seed_stations;

@ActiveProfiles("test")
@SpringBootTest
class SeedMigrationIntegrationTest {
  @Autowired DataSource dataSource;

  @Test
  void freshImportPreservesExistingIdsAndDoesNotReplayAfterEdits() throws Exception {
    withSchema(
        (connection, schema) -> {
          flyway(schema, "4").migrate();
          try (var sql = connection.createStatement()) {
            sql.execute(
                "INSERT INTO station (id, name, x, y) VALUES (1, 'Existing station', 0, 0)");
          }
          var migration = flyway(schema, "5");
          migration.migrate();
          assertEquals(15, number(connection, "SELECT count(*) FROM station"));
          assertEquals(
              1,
              number(connection, "SELECT count(*) FROM station WHERE name = 'Existing station'"));
          assertEquals(
              2, number(connection, "SELECT count(*) FROM team_members WHERE team_id = 1"));
          assertEquals(
              1,
              number(
                  connection,
                  "SELECT count(*) FROM flyway_schema_history WHERE version = '5' AND checksum IS NOT NULL"));
          try (var sql = connection.createStatement()) {
            sql.execute("UPDATE station SET name = 'Edited station' WHERE id = 2");
            sql.execute("DELETE FROM station WHERE id = 3");
            sql.execute("INSERT INTO station (name, x, y) VALUES ('Generated station', 0, 0)");
          }
          assertTrue(
              number(connection, "SELECT id FROM station WHERE name = 'Generated station'") > 15);
          assertEquals(0, migration.migrate().migrationsExecuted);
          assertEquals(
              1, number(connection, "SELECT count(*) FROM station WHERE name = 'Edited station'"));
          assertEquals(0, number(connection, "SELECT count(*) FROM station WHERE id = 3"));
        });
  }

  @Test
  void failedImportRollsBackAndCanBeRetried() throws Exception {
    withSchema(
        (connection, schema) -> {
          flyway(schema, "4").migrate();
          try (var sql = connection.createStatement()) {
            sql.execute(
                "ALTER TABLE station ADD CONSTRAINT reject_seed CHECK (name <> 'Quantum Commons')");
          }
          var migration = flyway(schema, "5");
          assertThrows(FlywayException.class, migration::migrate);
          assertEquals(0, number(connection, "SELECT count(*) FROM station"));
          assertEquals(
              0,
              number(connection, "SELECT count(*) FROM flyway_schema_history WHERE version = '5'"));
          try (var sql = connection.createStatement()) {
            sql.execute("ALTER TABLE station DROP CONSTRAINT reject_seed");
          }
          migration.migrate();
          assertEquals(15, number(connection, "SELECT count(*) FROM station"));
          try (var sql = connection.createStatement()) {
            sql.execute(
                "UPDATE flyway_schema_history SET checksum = checksum + 1 WHERE version = '5'");
          }
          assertThrows(FlywayException.class, migration::validate);
        });
  }

  @Test
  void rejectsMalformedCsvAndChecksumsInputChanges() throws Exception {
    var original =
        new ClassPathResource("seed/v5/stops.csv").getContentAsString(StandardCharsets.UTF_8);
    var checksum = new V5__seed_stations().getChecksum();
    withSchema(
        (connection, schema) -> {
          flyway(schema, "4").migrate();
          var thread = Thread.currentThread();
          var loader = thread.getContextClassLoader();
          for (var invalid :
              new String[] {
                original.replace("id,name,", "id,wrong_name,"),
                original.replaceFirst(",false,", ",not_a_boolean,")
              }) {
            try {
              thread.setContextClassLoader(
                  new ClassLoader(loader) {
                    @Override
                    public InputStream getResourceAsStream(String name) {
                      return name.equals("seed/v5/stops.csv")
                          ? new ByteArrayInputStream(invalid.getBytes(StandardCharsets.UTF_8))
                          : super.getResourceAsStream(name);
                    }
                  });
              assertNotEquals(checksum, new V5__seed_stations().getChecksum());
              assertThrows(FlywayException.class, () -> flyway(schema, "5").migrate());
              assertEquals(0, number(connection, "SELECT count(*) FROM station"));
            } finally {
              thread.setContextClassLoader(loader);
            }
          }
          flyway(schema, "5").migrate();
          assertEquals(15, number(connection, "SELECT count(*) FROM station"));
        });
  }

  private Flyway flyway(String schema, String target) {
    return Flyway.configure()
        .dataSource(dataSource)
        .schemas(schema)
        .defaultSchema(schema)
        .locations("classpath:db/migration", "classpath:seed")
        .target(target)
        .load();
  }

  private long number(Connection connection, String query) throws Exception {
    try (var statement = connection.createStatement();
        var rows = statement.executeQuery(query)) {
      assertTrue(rows.next());
      return rows.getLong(1);
    }
  }

  private void withSchema(Scenario scenario) throws Exception {
    var schema = "seed_test_" + UUID.randomUUID().toString().replace("-", "");
    try (var connection = dataSource.getConnection();
        var statement = connection.createStatement()) {
      var original = connection.getSchema();
      statement.execute("CREATE SCHEMA " + schema);
      try {
        connection.setSchema(schema);
        scenario.run(connection, schema);
      } finally {
        connection.setSchema(original);
        statement.execute("DROP SCHEMA " + schema + " CASCADE");
      }
    }
  }

  private interface Scenario {
    void run(Connection connection, String schema) throws Exception;
  }
}
