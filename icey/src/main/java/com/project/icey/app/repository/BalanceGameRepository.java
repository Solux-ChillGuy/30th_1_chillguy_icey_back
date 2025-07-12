package com.project.icey.app.repository;

import com.project.icey.app.domain.BalanceGame;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BalanceGameRepository extends JpaRepository<BalanceGame, Long> {
    long countByTeam_TeamId(Long teamId);
    List<BalanceGame> findByTeam_TeamId(Long teamId);
}



