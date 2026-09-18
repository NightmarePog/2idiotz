package cloud.twoidiotz.app.v1.domain.station;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Data;

@Data
@Entity(name = "Station")
public class StationModel {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @Size(min = 1, max = 255)
  private String name;

  @Size(max = 255)
  private String imageUrl;

  private boolean isTransfer;

  private BigDecimal x;
  private BigDecimal y;

  private boolean wheelchairAccessible;
  private boolean hasShelter;
  private boolean hasBench;
  private boolean hasTicketMachine;
  private boolean hasDisplay;
}
