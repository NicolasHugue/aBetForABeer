package project.web.backendBet.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "team")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private int points = 0;
    @Column(nullable = false)
    private int totalMatch = 0;
    @Column(nullable = false)
    private int wins = 0;
    @Column(nullable = false)
    private int draws = 0;
    @Column(nullable = false)
    private int losses = 0;
    @Column(nullable = false)
    private int goalsFor = 0;
    @Column(nullable = false)
    private int goalsAgainst = 0;

    @Transient
    @com.fasterxml.jackson.annotation.JsonProperty("goalDifference")
    public int getGoalDifference() {
        return goalsFor - goalsAgainst;
    }

}
