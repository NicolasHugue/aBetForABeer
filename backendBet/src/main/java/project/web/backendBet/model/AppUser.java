package project.web.backendBet.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AppUser {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true, length=50)
    private String username;

    @Column(nullable=false, length=100)
    private String password; // bcrypt

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=10)
    private Role role;

    @Column(nullable=false)
    private boolean enabled = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable=false, updatable=false)
    private OffsetDateTime createdAt;
}
