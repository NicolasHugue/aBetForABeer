package project.web.backendBet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.web.backendBet.db.TeamRepository;
import project.web.backendBet.model.Team;

import java.util.List;

@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    public List<Team> getTeamsByPointsDesc() {
        return teamRepository.findAllByOrderByPointsDesc();
    }
}
