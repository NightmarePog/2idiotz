package cloud.twoidiotz;

import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {
    private final JdbcTemplate jdbc;

    public ApiController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public record Greeting(String message) {}
    public record Health(String status, String database) {}

    @GetMapping("/api/hello")
    public Greeting hello() {
        return new Greeting("Hello from Spring Boot!");
    }

    @GetMapping("/api/health")
    public ResponseEntity<Health> health() {
        try {
            Integer result = jdbc.queryForObject("SELECT 1", Integer.class);
            if (Integer.valueOf(1).equals(result)) {
                return ResponseEntity.ok().header("Cache-Control", "no-store")
                        .body(new Health("UP", "UP"));
            }
        } catch (DataAccessException ignored) {
            // Never return database credentials or driver errors to the caller.
        }
        return ResponseEntity.status(503).header("Cache-Control", "no-store")
                .body(new Health("DOWN", "DOWN"));
    }
}
