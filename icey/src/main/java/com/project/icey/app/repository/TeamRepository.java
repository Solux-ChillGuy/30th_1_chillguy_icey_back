package com.project.icey.app.repository;

import com.project.icey.app.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {
    //추후 필요시 추가

    //invitation 링크로 팀 매칭
    Optional<Team> findByInvitation(String invitation);

    List<Team> findByExpirationBefore(LocalDateTime now);

    @Query("SELECT t FROM Team t JOIN FETCH t.members m JOIN FETCH m.user WHERE t.teamId = :teamId")
    Optional<Team> findByIdWithMembersAndUsers(@Param("teamId") Long teamId);

}
