package project.web.backendBet.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.web.backendBet.dto.CreateMatchRequest;
import project.web.backendBet.model.Match;
import project.web.backendBet.repo.MatchRepository;
import project.web.backendBet.service.MatchService;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchRepository matches;
    private final MatchService matchService;

    @GetMapping("/week/{week}")
    public List<Match> byWeek(@PathVariable int week) {
        return matches.findAll().stream().filter(m -> m.getWeek() == week).toList();
    }

    @PostMapping
    public ResponseEntity<Match> create(@Valid @RequestBody CreateMatchRequest req) {
        Match saved = matchService.create(req);
        return ResponseEntity.ok(saved);
    }
}