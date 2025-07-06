package com.project.icey.app.service;

import com.project.icey.app.domain.Team;
import com.project.icey.app.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamCleanupService {
    private final TeamRepository teamRepository;

    @Scheduled( cron = "0 0 0 * * *")//테스트 용으로 10초마다 삭제하도록(fixedDelay = 10000)
    @Transactional
    public void deleteExpiredTeams() {
        LocalDateTime now = LocalDateTime.now();
        List<Team> expiredTeams = teamRepository.findByExpirationBefore(now);


        if (!expiredTeams.isEmpty()) {
            System.out.println("[TeamCleanup] 만료된 팀 수: " + expiredTeams.size());
            /*for (Team t : expiredTeams) {
                System.out.println("삭제 대상 팀: " + t.getTeamName() + " / 만료일: " + t.getExpiration());
            }*/

            teamRepository.deleteAll(expiredTeams);
        } else {
            System.out.println("[TeamCleanup] 삭제할 만료 팀 없음 (현재 시간: " + now + ")");
        }
    }
}
