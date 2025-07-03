package com.project.icey.app.repository;

import com.project.icey.app.domain.Team;
import com.project.icey.app.domain.User;
import com.project.icey.app.domain.UserTeamManager;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserTeamRepository extends JpaRepository<UserTeamManager, Long> {
    //유저별 팀 정보 조회
    List<UserTeamManager> findByUser(User user);

    //팀별 멤버 목록 조회
    List<UserTeamManager> findByTeam_TeamId(Long teamId);

    //중복 가입 방지용
    boolean existsByUserAndTeam(User user, Team team);

    //팀별 멤버 전체 조회용-> 추후 개선 필요할 수 있습니다
    List<UserTeamManager> findByTeam(Team team);
}