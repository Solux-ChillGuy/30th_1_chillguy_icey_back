package com.project.icey.app.repository;

import com.project.icey.app.domain.BalanceGameVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BalanceGameVoteRepository extends JpaRepository<BalanceGameVote, Long> {
    boolean existsByBalanceGameIdAndUserId(Long balanceGameId, Long userId);
    Optional<BalanceGameVote> findByBalanceGameIdAndUserId(Long balanceGameId, Long userId);

    @Query("SELECT v.selectedOption, COUNT(v) FROM BalanceGameVote v WHERE v.balanceGame.id = :gameId GROUP BY v.selectedOption")
    List<Object[]> countVotesByGameId(@Param("gameId") Long gameId);
}