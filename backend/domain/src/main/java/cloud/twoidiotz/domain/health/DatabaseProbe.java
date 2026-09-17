package cloud.twoidiotz.domain.health;

/** Checks availability without exposing database implementation details. */
public interface DatabaseProbe {
  boolean isAvailable();
}
