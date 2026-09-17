package cloud.twoidiotz.app.health;

import cloud.twoidiotz.domain.health.HealthStatus;

public record HealthResponse(HealthStatus status, HealthStatus database) {}
