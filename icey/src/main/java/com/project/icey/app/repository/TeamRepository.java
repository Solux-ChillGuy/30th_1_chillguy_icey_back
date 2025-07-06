package com.project.icey.app.repository;

import com.project.icey.app.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {
    //추후 필요시 추가

    //invitation 링크로 팀 매칭
    Optional<Team> findByInvitation(String invitation);

    List<Team> findByExpirationBefore(LocalDateTime now);
}
