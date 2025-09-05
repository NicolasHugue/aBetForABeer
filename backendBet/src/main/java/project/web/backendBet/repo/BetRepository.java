package project.web.backendBet.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import project.web.backendBet.model.Bet;

import java.util.List;
import java.util.Optional;

public interface BetRepository extends JpaRepository<Bet, Long> {

    @Query("""
           SELECT b FROM Bet b
           JOIN FETCH b.user u
           WHERE b.match.id = :matchId
           ORDER BY b.createdAt ASC, u.username ASC
           """)
    List<Bet> findAllForMatchWithUser(Long matchId);

    Optional<Bet> findByMatch_IdAndUser_Id(Long matchId, Long userId);

    boolean existsByMatch_IdAndUser_Id(Long matchId, Long userId);
}
