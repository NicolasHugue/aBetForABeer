package project.web.backendBet.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.web.backendBet.model.Team;
import project.web.backendBet.service.TeamService;

import java.util.List;

@RestController
@RequestMapping(path = "/api/teams")
public class TeamController {


    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping("/ranking")
    public List<Team> getTeamRanking() {
        return teamService.getRanking();
    }
}
