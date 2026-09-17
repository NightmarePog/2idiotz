package cloud.twoidiotz.app.v1.domain.station;

import cloud.twoidiotz.app.api.SnakeCase;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@SnakeCase
@Schema(name = "StopInput")
public record StationRequest(
    @NotBlank @Size(max = 255) String name,
    @Size(max = 255) @Schema(types = {"string", "null"}) String imageUrl,
    @NotNull Boolean wheelchairAccessible,
    @NotNull Boolean hasShelter,
    @NotNull Boolean hasTicketMachine) {}
