package cloud.twoidiotz.domain.health;

public final class HealthService {
  private final DatabaseProbe database;

  public HealthService(DatabaseProbe database) {
    this.database = database;
  }

  public HealthStatus check() {
    return database.isAvailable() ? HealthStatus.UP : HealthStatus.DOWN;
  }
}
