package project.web.backendBet.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "matches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "home_team_id", nullable = false)
    @ToString.Exclude
    private Team homeTeam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "away_team_id", nullable = false)
    @ToString.Exclude
    private Team awayTeam;

    @Column(nullable = false)
    private OffsetDateTime matchDate;
    @Column(nullable = false)
    private int week;

    private int homeGoals;
    private int awayGoals;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private MatchStatus status = MatchStatus.SCHEDULED;


}