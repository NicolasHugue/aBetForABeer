package project.web.backendBet.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import project.web.backendBet.model.Team;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {

    @Query("""
              SELECT t FROM Team t
              ORDER BY t.points DESC,
                      (t.goalsFor - t.goalsAgainst) DESC,
                      t.goalsFor DESC,
                      t.name ASC
            """)
    List<Team> ranking();
}
