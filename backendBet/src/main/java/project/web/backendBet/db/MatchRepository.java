package project.web.backendBet.db;

import org.springframework.data.jpa.repository.JpaRepository;
import project.web.backendBet.model.Match;

public interface MatchRepository extends JpaRepository<Match,Long> {
}
