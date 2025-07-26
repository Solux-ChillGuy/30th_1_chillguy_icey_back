package com.project.icey.app.service;

import com.project.icey.app.domain.NotificationType;
import com.project.icey.app.domain.Team;
import com.project.icey.app.domain.UserTeamManager;
import com.project.icey.app.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamCleanupService {
    private final TeamRepository teamRepository;
    private final NotificationService notificationService;

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

    //만료 3일전 1일전 알림 발송
    @Transactional
    @Scheduled(cron = "0 0 0 * * *") //(fixedDelay = 10000)
    public void notifyTeamExpiration() {
        LocalDate now = LocalDate.now();
        List<Team> teams = teamRepository.findAll();

        for (Team team : teams) {
            long daysLeft = ChronoUnit.DAYS.between(now, team.getExpiration().toLocalDate());

            if (daysLeft == 3 || daysLeft == 1) {
                NotificationType notificationType = null;
                if (daysLeft == 3) {
                    notificationType = NotificationType.TEAM_EXPIRATION_3;
                } else {
                    notificationType = NotificationType.TEAM_EXPIRATION_1;
                }

                // 팀 멤버들 가져오기
                List<UserTeamManager> members = team.getMembers();

                // 각 멤버에게 알림 전송
                for (UserTeamManager member : members) {
                    notificationService.sendNotification(
                            member.getUser().getId(),
                            notificationType,
                            team.getTeamName()
                    );
                }
            }
        }
    }
}
