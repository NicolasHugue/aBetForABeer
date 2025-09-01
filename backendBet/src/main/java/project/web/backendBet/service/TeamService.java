package project.web.backendBet.service;

import org.springframework.stereotype.Service;
import project.web.backendBet.model.Team;
import project.web.backendBet.repo.TeamRepository;

import java.util.List;

@Service
public class TeamService {

    private final TeamRepository teamRepository;

    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    public List<Team> getRanking() {
        return teamRepository.ranking();
    }
}
