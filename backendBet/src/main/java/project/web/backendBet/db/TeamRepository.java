package project.web.backendBet.db;

import org.springframework.data.jpa.repository.JpaRepository;
import project.web.backendBet.model.Team;

public interface TeamRepository extends JpaRepository<Team,Long> {
}
