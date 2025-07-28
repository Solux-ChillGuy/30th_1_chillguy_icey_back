package com.project.icey.app.repository;

import com.project.icey.app.domain.Card;
import com.project.icey.app.domain.Team;
import com.project.icey.app.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {

    // 템플릿 목록
    List<Card> findByUserIdAndTeamIsNull(Long userId);

    // 팀 명함 목록
    List<Card>  findByTeam_TeamId(Long teamId);

    // 팀 명함 목록 (팀 ID와 사용자 ID로 조회):특정 팀+특정 유저"가 가진 명함 찾기 (팀에서 내 명함 1장)
    Optional<Card> findByTeam_TeamIdAndUserId(Long teamId, Long userId);

    //이 템플릿에서 복사된 팀카드들 다 찾기
    List<Card> findByOriginId(Long originId);
    //이 템플릿을 쓰는 팀카드가 하나라도 있는지?
    boolean existsByOriginId(Long originId);

    // originId(템플릿 id)와 userId(내 id)로만 필터링!
    List<Card> findByOriginIdAndUserId(Long originId, Long userId);


    Optional<Card> findByUserAndTeam(User user, Team team);
}

//findByTeamId
//findByTeamIdAndUserId