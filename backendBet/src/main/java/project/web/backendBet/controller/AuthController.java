package project.web.backendBet.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import project.web.backendBet.model.AppUser;
import project.web.backendBet.model.Role;
import project.web.backendBet.repo.UserRepository;
import project.web.backendBet.security.JwtService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final UserRepository users;
    private final PasswordEncoder encoder;

    // --- Login ---
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest req) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username(), req.password()));
        User principal = (User) auth.getPrincipal();

        var dbUser = users.findByUsername(principal.getUsername()).orElseThrow();
        String token = jwtService.generateToken(dbUser.getUsername(), dbUser.getRole());
        return ResponseEntity.ok(new AuthResponse(dbUser.getUsername(), dbUser.getRole().name(), token));
    }

    // --- Me ---
    @GetMapping("/me")
    public ResponseEntity<AuthUser> me(@AuthenticationPrincipal User principal) {
        if (principal == null) return ResponseEntity.status(401).build();
        var dbUser = users.findByUsername(principal.getUsername()).orElseThrow();
        return ResponseEntity.ok(new AuthUser(dbUser.getUsername(), dbUser.getRole().name()));
    }

    // --- Register (USER only) ---
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        if (users.existsByUsername(req.username())) {
            return ResponseEntity.badRequest().body("username already exists");
        }
        var user = AppUser.builder()
                .username(req.username())
                .password(encoder.encode(req.password()))
                .role(Role.USER) // ← FORCÉ : pas d'admin via inscription
                .enabled(true)
                .build();
        users.save(user);
        return ResponseEntity.ok().build();
    }

    // --- DTOs ---
    public record LoginRequest(String username, String password) {}

    public record AuthResponse(String username, String role, String token) {}

    public record AuthUser(String username, String role) {}

    public record RegisterRequest(
            @NotBlank
            @Size(min = 3, max = 50)
            @Pattern(regexp = "^[a-zA-Z0-9_.-]+$", message = "username contains invalid characters")
            String username,
            @NotBlank
            @Size(min = 6, max = 100)
            String password
    ) {}
}