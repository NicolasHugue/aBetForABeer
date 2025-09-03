package project.web.backendBet.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import project.web.backendBet.model.AppUser;

import java.util.Optional;

public interface UserRepository extends JpaRepository <AppUser,Long> {
    Optional<AppUser> findByUsername(String username);
    boolean existsByUsername(String username);
}
