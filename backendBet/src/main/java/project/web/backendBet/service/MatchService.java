package project.web.backendBet.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import project.web.backendBet.dto.CreateMatchRequest;
import project.web.backendBet.dto.MatchSummary;
import project.web.backendBet.dto.SetScoreRequest;
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

    @Transactional
    public Match setScore(SetScoreRequest req) {
        Match m = matchRepo.findById(req.matchId())
                .orElseThrow(() -> new IllegalArgumentException("Match introuvable"));

        Team home = m.getHomeTeam();
        Team away = m.getAwayTeam();

        // 1) Si le match avait déjà un score final, on annule son impact
        if (m.getStatus() == MatchStatus.FINISHED) {
            applyResult(home, away, m.getHomeGoals(), m.getAwayGoals(), -1);
        }

        // 2) Appliquer le nouveau score
        m.setHomeGoals(req.homeGoals());
        m.setAwayGoals(req.awayGoals());
        m.setStatus(MatchStatus.FINISHED);
        applyResult(home, away, req.homeGoals(), req.awayGoals(), +1);

        // 3) Sauvegarde
        teamRepo.save(home);
        teamRepo.save(away);
        return m; // sera flush avec la transaction
    }

    /**
     * Applique (sign=+1) ou annule (sign=-1) l'impact d'un résultat sur les deux équipes.
     */
    private void applyResult(Team home, Team away, int hg, int ag, int sign) {
        // buts
        home.setGoalsFor( home.getGoalsFor()      + sign * hg );
        home.setGoalsAgainst( home.getGoalsAgainst() + sign * ag );
        away.setGoalsFor( away.getGoalsFor()      + sign * ag );
        away.setGoalsAgainst( away.getGoalsAgainst() + sign * hg );

        // matches joués
        home.setTotalMatch( home.getTotalMatch() + sign );
        away.setTotalMatch( away.getTotalMatch() + sign );

        // résultat
        if (hg > ag) {
            // victoire domicile
            home.setWins( home.getWins() + sign );
            away.setLosses( away.getLosses() + sign );
            home.setPoints( home.getPoints() + 3 * sign );
        } else if (hg < ag) {
            // victoire extérieur
            away.setWins( away.getWins() + sign );
            home.setLosses( home.getLosses() + sign );
            away.setPoints( away.getPoints() + 3 * sign );
        } else {
            // nul
            home.setDraws( home.getDraws() + sign );
            away.setDraws( away.getDraws() + sign );
            home.setPoints( home.getPoints() + 1 * sign );
            away.setPoints( away.getPoints() + 1 * sign );
        }

        // garde-fous (éviter valeurs négatives en cas de corrections)
        clampNonNegative(home);
        clampNonNegative(away);
    }

    private void clampNonNegative(Team t) {
        if (t.getGoalsFor()      < 0) t.setGoalsFor(0);
        if (t.getGoalsAgainst()  < 0) t.setGoalsAgainst(0);
        if (t.getTotalMatch()    < 0) t.setTotalMatch(0);
        if (t.getWins()          < 0) t.setWins(0);
        if (t.getDraws()         < 0) t.setDraws(0);
        if (t.getLosses()        < 0) t.setLosses(0);
        if (t.getPoints()        < 0) t.setPoints(0);
    }
}
