package project.web.backendBet.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "bets",
        uniqueConstraints = @UniqueConstraint(name = "uq_bet_user_match", columnNames = {"user_id","match_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Bet {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="match_id", nullable=false)
    private Match match;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="user_id", nullable=false)
    private AppUser user;

    @Column(name="predicted_home_goals", nullable=false)
    private int predictedHomeGoals;

    @Column(name="predicted_away_goals", nullable=false)
    private int predictedAwayGoals;

    @Column(name="created_at", nullable=false)
    private OffsetDateTime createdAt = OffsetDateTime.now();
}
