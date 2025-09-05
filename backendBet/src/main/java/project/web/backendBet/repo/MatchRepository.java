package project.web.backendBet.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.web.backendBet.model.Match;

public interface MatchRepository extends JpaRepository<Match, Long> {

    @Query("""
           SELECT COUNT(m) > 0
           FROM Match m
           WHERE m.week = :week
             AND (m.homeTeam.id = :teamId OR m.awayTeam.id = :teamId)
           """)
    boolean existsTeamScheduledInWeek(@Param("week") int week, @Param("teamId") Long teamId);
}
