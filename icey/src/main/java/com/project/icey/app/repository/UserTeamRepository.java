package com.project.icey.app.repository;

import com.project.icey.app.domain.Team;
import com.project.icey.app.domain.User;
import com.project.icey.app.domain.UserRole;
import com.project.icey.app.domain.UserTeamManager;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserTeamRepository extends JpaRepository<UserTeamManager, Long> {
    //유저별 팀 정보 조회
    List<UserTeamManager> findByUser(User user);

    //중복 가입 방지용
    boolean existsByUserAndTeam(User user, Team team);

    Optional<UserTeamManager> findByUserAndTeam(User user, Team team);

    List<UserTeamManager> findAllByTeam(Team team);

    void deleteByUserAndTeam(User user, Team team);

    //팀 별 멤버수 조회하는 메서드
    int countByTeam(Team team);

    Optional<UserTeamManager> findByUserIdAndTeam_TeamId(Long userId, Long teamId);

}