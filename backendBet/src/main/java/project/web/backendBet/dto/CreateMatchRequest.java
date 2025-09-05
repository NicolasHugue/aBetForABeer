package project.web.backendBet.dto;

import jakarta.validation.constraints.*;
import java.time.OffsetDateTime;

public record CreateMatchRequest(
        @NotNull Long homeTeamId,
        @NotNull Long awayTeamId,
        @Min(1) @Max(52) int week,
        @NotNull OffsetDateTime matchDate
) {}
