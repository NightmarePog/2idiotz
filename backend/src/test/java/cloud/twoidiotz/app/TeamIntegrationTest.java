package cloud.twoidiotz.app;

import static org.junit.jupiter.api.Assertions.*;

import cloud.twoidiotz.app.v1.domain.team.TeamModel;
import cloud.twoidiotz.app.v1.domain.team.TeamRepository;
import cloud.twoidiotz.app.v1.domain.team.TeamResponse;
import java.net.URI;
import java.net.http.*;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import tools.jackson.databind.json.JsonMapper;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TeamIntegrationTest {
  @LocalServerPort int port;
  @Autowired TeamRepository teams;
  @Autowired org.flywaydb.core.Flyway flyway;
  @Autowired PlatformTransactionManager transactions;

  @Test
  void readsCommittedJpaWritesThroughHttp() throws Exception {
    var tx = new TransactionTemplate(transactions);
    var original =
        tx.execute(
            status ->
                teams
                    .findById(1L)
                    .map(team -> new TeamResponse(team.getName(), List.copyOf(team.getMembers())))
                    .orElse(null));
    try {
      saveTeam("Database team", List.of("Žofie", "Jan"));
      assertTeam("Database team", List.of("Žofie", "Jan"));
      saveTeam("Updated in PostgreSQL", List.of("Eva"));
      flyway.migrate();
      assertTeam("Updated in PostgreSQL", List.of("Eva"));
    } finally {
      if (original == null) teams.deleteById(1L);
      else saveTeam(original.name(), original.members());
    }
  }

  private void saveTeam(String name, List<String> members) {
    var team = new TeamModel();
    team.setId(1L);
    team.setName(name);
    team.setMembers(members);
    teams.saveAndFlush(team);
  }

  private void assertTeam(String name, List<String> members) throws Exception {
    var response =
        HttpClient.newHttpClient()
            .send(
                HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api/v1/team"))
                    .build(),
                HttpResponse.BodyHandlers.ofString());
    assertEquals(200, response.statusCode());
    assertEquals("no-store", response.headers().firstValue("Cache-Control").orElseThrow());
    assertEquals(
        new TeamResponse(name, members),
        JsonMapper.builder().build().readValue(response.body(), TeamResponse.class));
  }
}
