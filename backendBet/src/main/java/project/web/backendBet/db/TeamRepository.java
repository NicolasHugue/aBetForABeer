package project.web.backendBet.db;

import org.springframework.data.jpa.repository.JpaRepository;
import project.web.backendBet.model.Team;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team,Long> {

    List<Team> findAllByOrderByPointsDesc();
}
