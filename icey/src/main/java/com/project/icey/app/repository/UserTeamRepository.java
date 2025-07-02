package com.project.icey.app.repository;

import com.project.icey.app.domain.UserTeamManager;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserTeamRepository extends JpaRepository<UserTeamManager, Long> {
    //유저별 팀 정보 조회
    List<UserTeamManager> findByUserId(String userId);

    //팀별 멤버 목록 조회
    List<UserTeamManager> findByTeamId(String teamId);
}
