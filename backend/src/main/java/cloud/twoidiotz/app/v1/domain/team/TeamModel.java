package cloud.twoidiotz.app.v1.domain.team;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
@Entity(name = "Team")
public class TeamModel {
  @Id private Long id;

  private String name;

  @ElementCollection private List<String> members = new ArrayList<>();
}
