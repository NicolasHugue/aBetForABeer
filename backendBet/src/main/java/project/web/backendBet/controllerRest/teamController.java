package project.web.backendBet.controllerRest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.web.backendBet.model.Team;
import project.web.backendBet.service.TeamService;

import java.util.List;

@RestController
@RequestMapping(path = "/api/teams")
@CrossOrigin(origins = "*")
public class teamController {

    @Autowired
    private TeamService teamService;

    @GetMapping("/ranking")
    public List<Team> getTeamRanking (){
        return teamService.getTeamsByPointsDesc();
    }
}
