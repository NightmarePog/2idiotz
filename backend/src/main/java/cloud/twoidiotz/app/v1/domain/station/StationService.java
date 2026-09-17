package cloud.twoidiotz.app.v1.domain.station;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StationService {
  private final StationRepository stations;

  public List<StationResponse> list() {
    return stations.findAll(Sort.by("id")).stream().map(StationService::toResponse).toList();
  }

  public StationResponse get(long id) {
    return toResponse(find(id));
  }

  @Transactional
  public StationResponse create(StationRequest input) {
    var station = new StationModel();
    apply(input, station);
    return toResponse(stations.save(station));
  }

  @Transactional
  public StationResponse update(long id, StationRequest input) {
    var station = find(id);
    apply(input, station);
    return toResponse(station);
  }

  @Transactional
  public void delete(long id) {
    stations.delete(find(id));
  }

  private StationModel find(long id) {
    return stations.findById(id).orElseThrow(() -> new StationNotFoundException(id));
  }

  private static void apply(StationRequest input, StationModel station) {
    station.setName(input.name());
    station.setImageUrl(input.imageUrl());
    station.setWheelchairAccessible(input.wheelchairAccessible());
    station.setHasShelter(input.hasShelter());
    station.setHasTicketMachine(input.hasTicketMachine());
  }

  private static StationResponse toResponse(StationModel station) {
    return new StationResponse(
        station.getId(),
        station.getName(),
        station.getImageUrl(),
        station.isWheelchairAccessible(),
        station.isHasShelter(),
        station.isHasTicketMachine());
  }
}
