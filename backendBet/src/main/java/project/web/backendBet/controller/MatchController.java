package project.web.backendBet.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.web.backendBet.dto.CreateMatchRequest;
import project.web.backendBet.dto.MatchSummary;
import project.web.backendBet.model.Match;
import project.web.backendBet.service.MatchService;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @GetMapping("/week/{week}")
    public List<MatchSummary> byWeek(@PathVariable int week) {
        return matchService.listByWeek(week);
    }

    @PostMapping
    public ResponseEntity<Match> create(@Valid @RequestBody CreateMatchRequest req) {
        return ResponseEntity.ok(matchService.create(req));
    }
}