package cloud.twoidiotz.app.v1.domain.station;

import cloud.twoidiotz.app.api.SnakeCase;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.net.URI;
import java.util.Set;
import tools.jackson.databind.JsonNode;

@SnakeCase
@Schema(name = "StopInput", additionalProperties = Schema.AdditionalPropertiesValue.FALSE)
public record StationRequest(
    @NotNull @Size(min = 1, max = 255) String name,
    @Size(max = 255)
        @Schema(
            types = {"string", "null"},
            format = "uri")
        String imageUrl,
    @NotNull Boolean wheelchairAccessible,
    @NotNull Boolean hasShelter,
    @NotNull Boolean hasTicketMachine) {
  // Inspect the original JSON types before Jackson can coerce strings or numbers.
  @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
  public static StationRequest fromJson(JsonNode input) {
    var fields =
        Set.of("name", "image_url", "wheelchair_accessible", "has_shelter", "has_ticket_machine");
    if (!input.isObject() || !fields.containsAll(input.propertyNames())) {
      throw new IllegalArgumentException("Invalid stop fields");
    }
    var name = input.get("name");
    if (name == null || !name.isString()) throw new IllegalArgumentException("Invalid name");
    for (var field : Set.of("wheelchair_accessible", "has_shelter", "has_ticket_machine")) {
      if (!input.has(field) || !input.get(field).isBoolean()) {
        throw new IllegalArgumentException("Invalid boolean field: " + field);
      }
    }
    var image = input.get("image_url");
    String imageUrl = null;
    if (image != null && !image.isNull()) {
      if (!image.isString()) throw new IllegalArgumentException("Invalid image URL");
      imageUrl = image.asString();
      var uri = URI.create(imageUrl);
      if (!uri.isAbsolute()
          || ((uri.getScheme().equalsIgnoreCase("http")
                  || uri.getScheme().equalsIgnoreCase("https")
                  || uri.getScheme().equalsIgnoreCase("ftp"))
              && uri.getHost() == null)) throw new IllegalArgumentException("Invalid image URL");
    }
    return new StationRequest(
        name.asString(),
        imageUrl,
        input.get("wheelchair_accessible").asBoolean(),
        input.get("has_shelter").asBoolean(),
        input.get("has_ticket_machine").asBoolean());
  }
}
