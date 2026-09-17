package seed;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.CRC32;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.postgresql.PGConnection;
import org.springframework.core.io.ClassPathResource;

public class V5__seed_stations extends BaseJavaMigration {
  private static final String COPY =
      "COPY seed_station FROM STDIN WITH (FORMAT csv, HEADER MATCH, ENCODING 'UTF8')";

  @Override
  public Integer getChecksum() {
    var checksum = new CRC32();
    checksum.update(COPY.getBytes(StandardCharsets.UTF_8));
    try {
      for (var file : new String[] {"stage.sql", "stops.csv", "insert.sql"}) {
        checksum.update(file.getBytes(StandardCharsets.UTF_8));
        checksum.update(
            resource(file)
                .getContentAsString(StandardCharsets.UTF_8)
                .replace("\r\n", "\n")
                .getBytes(StandardCharsets.UTF_8));
      }
    } catch (IOException exception) {
      throw new UncheckedIOException("Cannot checksum the V5 seed resources", exception);
    }
    return (int) checksum.getValue();
  }

  @Override
  public void migrate(Context context) throws Exception {
    var connection = context.getConnection();
    try (var statement = connection.createStatement();
        var input = resource("stops.csv").getInputStream()) {
      statement.execute(resource("stage.sql").getContentAsString(StandardCharsets.UTF_8));
      connection.unwrap(PGConnection.class).getCopyAPI().copyIn(COPY, input);
      statement.execute(resource("insert.sql").getContentAsString(StandardCharsets.UTF_8));
    }
  }

  private static ClassPathResource resource(String file) {
    return new ClassPathResource("seed/v5/" + file);
  }
}
