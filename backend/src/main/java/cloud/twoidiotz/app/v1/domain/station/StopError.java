package cloud.twoidiotz.app.v1.domain.station;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
    name = "Error",
    additionalProperties = Schema.AdditionalPropertiesValue.FALSE,
    requiredProperties = {"error"})
public record StopError(@Schema(example = "Stop not found") String error) {}
