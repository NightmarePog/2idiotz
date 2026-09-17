package cloud.twoidiotz.app.v1.domain.station;

import cloud.twoidiotz.app.api.ApiDelete;
import cloud.twoidiotz.app.api.ApiGet;
import cloud.twoidiotz.app.api.ApiPost;
import cloud.twoidiotz.app.api.ApiPut;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Stops", description = "Stop management")
@RequiredArgsConstructor
public class StationController {
  private final StationService stations;

  @ApiGet(value = "/stops", summary = "List all stops", responseDescription = "List of stops")
  public ResponseEntity<List<StationResponse>> getStops() {
    return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(stations.list());
  }

  @ApiGet(value = "/stops/{id}", summary = "Get stop detail", responseDescription = "Stop detail")
  public ResponseEntity<StationResponse> getStop(
      @Parameter(
              description = "Stop ID",
              example = "12",
              schema = @Schema(type = "integer", implementation = java.math.BigInteger.class))
          @PathVariable
          long id) {
    return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(stations.get(id));
  }

  @ApiPost(
      value = "/stops",
      summary = "Create a new stop",
      responseDescription = "Stop created successfully")
  public ResponseEntity<StationResponse> createStop(@Valid @RequestBody StationRequest input) {
    var stop = stations.create(input);
    return ResponseEntity.created(URI.create("/api/v1/stops/" + stop.id())).body(stop);
  }

  @ApiPut(
      value = "/stops/{id}",
      summary = "Update a stop",
      responseDescription = "Stop updated successfully")
  public StationResponse updateStop(
      @Parameter(
              description = "Stop ID",
              example = "12",
              schema = @Schema(type = "integer", implementation = java.math.BigInteger.class))
          @PathVariable
          long id,
      @Valid @RequestBody StationRequest input) {
    return stations.update(id, input);
  }

  @ApiDelete(
      value = "/stops/{id}",
      summary = "Delete a stop",
      responseDescription = "Stop deleted successfully")
  public void deleteStop(
      @Parameter(
              description = "Stop ID",
              example = "12",
              schema = @Schema(type = "integer", implementation = java.math.BigInteger.class))
          @PathVariable
          long id) {
    stations.delete(id);
  }
}
