package cloud.twoidiotz.app.v1.domain.station;

import cloud.twoidiotz.app.api.SnakeCase;
import io.swagger.v3.oas.annotations.media.Schema;

@SnakeCase
@Schema(
    name = "Stop",
    requiredProperties = {
      "id",
      "name",
      "image_url",
      "wheelchair_accessible",
      "has_shelter",
      "has_ticket_machine"
    })
public record StationResponse(
    @Schema(accessMode = Schema.AccessMode.READ_ONLY) long id,
    String name,
    @Schema(types = {"string", "null"}) String imageUrl,
    boolean wheelchairAccessible,
    boolean hasShelter,
    boolean hasTicketMachine) {}
