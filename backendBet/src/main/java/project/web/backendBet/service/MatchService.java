package project.web.backendBet.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import project.web.backendBet.dto.CreateMatchRequest;
import project.web.backendBet.dto.MatchSummary;
import project.web.backendBet.model.Match;
import project.web.backendBet.model.MatchStatus;
import project.web.backendBet.model.Team;
import project.web.backendBet.repo.MatchRepository;
import project.web.backendBet.repo.TeamRepository;

import java.util.List;

@Service
public class MatchService {
    private final MatchRepository matchRepo;
    private final TeamRepository teamRepo;

    public MatchService(MatchRepository matchRepo, TeamRepository teamRepo) {
        this.matchRepo = matchRepo;
        this.teamRepo = teamRepo;
    }

    @Transactional
    public Match create(CreateMatchRequest req) {
        if (req.homeTeamId().equals(req.awayTeamId())) {
            throw new IllegalArgumentException("Les équipes doivent être différentes");
        }

        if (matchRepo.existsTeamScheduledInWeek(req.week(), req.homeTeamId())) {
            throw new IllegalArgumentException("L'équipe à domicile a déjà un match cette semaine");
        }
        if (matchRepo.existsTeamScheduledInWeek(req.week(), req.awayTeamId())) {
            throw new IllegalArgumentException("L'équipe à l'extérieur a déjà un match cette semaine");
        }

        Team home = teamRepo.findById(req.homeTeamId())
                .orElseThrow(() -> new IllegalArgumentException("Equipe domicile introuvable"));
        Team away = teamRepo.findById(req.awayTeamId())
                .orElseThrow(() -> new IllegalArgumentException("Equipe visiteuse introuvable"));

        Match m = new Match();
        m.setHomeTeam(home);
        m.setAwayTeam(away);
        m.setWeek(req.week());
        m.setMatchDate(req.matchDate());
        m.setHomeGoals(0); // pas de score pour l’instant
        m.setAwayGoals(0);
        m.setStatus(MatchStatus.SCHEDULED);

        return matchRepo.save(m);
    }

    @Transactional
    public List<MatchSummary> listByWeek(int week) {
        return matchRepo.findByWeekWithTeams(week).stream()
                .map(m -> new MatchSummary(
                        m.getId(),
                        m.getWeek(),
                        m.getMatchDate(),
                        // m.getStatus() != null ? m.getStatus().name() : null
                        null, // ← si pas encore de champ status
                        m.getHomeTeam().getId(),
                        m.getHomeTeam().getName(),
                        m.getAwayTeam().getId(),
                        m.getAwayTeam().getName(),
                        m.getHomeGoals(),
                        m.getAwayGoals()
                ))
                .toList();
    }
}
