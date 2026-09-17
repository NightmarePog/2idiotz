package cloud.twoidiotz.app.v1.domain.team;

import cloud.twoidiotz.app.api.ApiGet;
import java.util.List;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class TeamController {
  private final TeamRepository teams;

  public TeamController(TeamRepository teams) {
    this.teams = teams;
  }

  @ApiGet("/team")
  @Transactional(readOnly = true)
  public ResponseEntity<TeamResponse> getTeam() {
    var team =
        teams.findById(1L).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    return ResponseEntity.ok()
        .cacheControl(CacheControl.noStore())
        .body(new TeamResponse(team.getName(), List.copyOf(team.getMembers())));
  }
}
