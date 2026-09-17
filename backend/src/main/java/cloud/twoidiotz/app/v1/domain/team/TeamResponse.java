package cloud.twoidiotz.app.v1.domain.team;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record TeamResponse(@NotBlank String name, @NotNull List<@NotBlank String> members) {}
