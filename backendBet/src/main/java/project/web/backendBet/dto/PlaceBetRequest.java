package project.web.backendBet.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PlaceBetRequest(
        @NotNull Long matchId,
        @Min(0) int homeGoals,
        @Min(0) int awayGoals
) {}