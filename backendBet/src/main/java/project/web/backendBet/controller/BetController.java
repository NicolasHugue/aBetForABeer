package project.web.backendBet.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import project.web.backendBet.dto.BetView;
import project.web.backendBet.dto.PlaceBetRequest;
import project.web.backendBet.service.BetService;

import java.util.List;

@RestController
@RequestMapping("/api/bets")
@RequiredArgsConstructor
public class BetController {

    private final BetService betService;

    // USER & ADMIN : placer un pari (avant coup d'envoi)
    @PostMapping
    public ResponseEntity<?> placeBet(@AuthenticationPrincipal User principal,
                                      @Valid @RequestBody PlaceBetRequest req) {
        if (principal == null) return ResponseEntity.status(401).build();
        var b = betService.place(principal.getUsername(), req);
        return ResponseEntity.ok().body(b.getId());
    }

    // USER & ADMIN : voir les paris d'un match (uniquement après le début)
    @GetMapping("/match/{matchId}")
    public List<BetView> listBets(@PathVariable Long matchId) {
        return betService.listForMatchIfStarted(matchId);
    }

    @GetMapping("/match/{matchId}/me")
    public ResponseEntity<BetView> myBet(@AuthenticationPrincipal User principal,
                                         @PathVariable Long matchId) {
        if (principal == null) return ResponseEntity.status(401).build();
        return betService.getMyBet(principal.getUsername(), matchId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build()); // 204 si pas de pari
    }
}
