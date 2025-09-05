package project.web.backendBet.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import project.web.backendBet.dto.BetView;
import project.web.backendBet.dto.PlaceBetRequest;
import project.web.backendBet.model.AppUser;
import project.web.backendBet.model.Bet;
import project.web.backendBet.model.Match;
import project.web.backendBet.repo.BetRepository;
import project.web.backendBet.repo.MatchRepository;
import project.web.backendBet.repo.UserRepository;

import java.time.Instant;
import java.util.List;

@Service
public class BetService {

    private final BetRepository bets;
    private final MatchRepository matches;
    private final UserRepository users;

    // blocage demandé : pas de paris sur l'équipe id=1 et nom exact ci-dessous
    private static final Long   BLOCKED_TEAM_ID   = 1L;
    private static final String BLOCKED_TEAM_NAME = "U.S. Ploegsteert-Bizet";

    public BetService(BetRepository bets, MatchRepository matches, UserRepository users) {
        this.bets = bets;
        this.matches = matches;
        this.users = users;
    }

    @Transactional
    public Bet place(String username, PlaceBetRequest req) {
        AppUser user = users.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));
        Match match = matches.findById(req.matchId())
                .orElseThrow(() -> new IllegalArgumentException("Match introuvable"));

        // 1) Interdire les paris après l'heure de début
        Instant now = Instant.now();
        if (now.isAfter(match.getMatchDate().toInstant())) {
            throw new IllegalArgumentException("Le match a commencé : les paris sont fermés");
        }

        // 2) Interdire les paris sur l'équipe bloquée (id=1 ou nom exact)
        boolean involvesBlocked =
                (match.getHomeTeam().getId().equals(BLOCKED_TEAM_ID)) ||
                        (match.getAwayTeam().getId().equals(BLOCKED_TEAM_ID)) ||
                        BLOCKED_TEAM_NAME.equals(match.getHomeTeam().getName()) ||
                        BLOCKED_TEAM_NAME.equals(match.getAwayTeam().getName());
        if (involvesBlocked) {
            throw new IllegalArgumentException("Les paris sont bloqués pour ce match");
        }

        // 3) Un seul pari par utilisateur et par match (unique constraint + check)
        if (bets.existsByMatch_IdAndUser_Id(match.getId(), user.getId())) {
            throw new IllegalArgumentException("Vous avez déjà parié sur ce match");
        }

        Bet b = new Bet();
        b.setMatch(match);
        b.setUser(user);
        b.setPredictedHomeGoals(req.homeGoals());
        b.setPredictedAwayGoals(req.awayGoals());
        return bets.save(b);
    }

    /** Visible seulement après le début du match */
    @Transactional
    public List<BetView> listForMatchIfStarted(Long matchId) {
        Match match = matches.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match introuvable"));

        if (Instant.now().isBefore(match.getMatchDate().toInstant())) {
            throw new IllegalArgumentException("Les paris ne sont visibles qu'après le début du match");
        }

        return bets.findAllForMatchWithUser(matchId).stream()
                .map(b -> new BetView(
                        b.getId(),
                        b.getUser().getUsername(),
                        b.getPredictedHomeGoals(),
                        b.getPredictedAwayGoals(),
                        b.getCreatedAt()
                ))
                .toList();
    }

    @Transactional
    public java.util.Optional<BetView> getMyBet(String username, Long matchId) {
        var user = users.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));
        return bets.findByMatch_IdAndUser_Id(matchId, user.getId())
                .map(b -> new BetView(
                        b.getId(),
                        user.getUsername(),
                        b.getPredictedHomeGoals(),
                        b.getPredictedAwayGoals(),
                        b.getCreatedAt()
                ));
    }

}
