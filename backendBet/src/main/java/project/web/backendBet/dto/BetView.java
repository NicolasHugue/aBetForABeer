package project.web.backendBet.dto;

import java.time.OffsetDateTime;

public record BetView(
        Long id,
        String username,
        int homeGoals,
        int awayGoals,
        OffsetDateTime createdAt
) {}
