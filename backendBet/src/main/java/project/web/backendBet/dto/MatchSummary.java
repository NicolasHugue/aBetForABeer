package project.web.backendBet.dto;


import java.time.OffsetDateTime;

public record MatchSummary(
        Long id,
        int week,
        OffsetDateTime matchDate,
        String status,
        Long homeTeamId,
        String homeTeamName,
        Long awayTeamId,
        String awayTeamName,
        int homeGoals,
        int awayGoals

) {
}
