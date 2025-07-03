package com.project.icey.app.service;

import com.project.icey.app.domain.Team;
import com.project.icey.app.domain.User;
import com.project.icey.app.domain.UserRole;
import com.project.icey.app.domain.UserTeamManager;
import com.project.icey.app.dto.*;
import com.project.icey.app.repository.TeamRepository;
import com.project.icey.app.repository.UserRepository;
import com.project.icey.app.repository.UserTeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository teamRepository;
    private final UserTeamRepository userteamRepository;

    public TeamResponse createTeam(CreateTeamRequest request, User creator){
        Team team = Team.builder() //
                .teamName(request.getTeamName())
                .memberNum(request.getMemberNum())
                .invitation(UUID.randomUUID().toString())
                .build();

        teamRepository.save(team);

        UserTeamManager utm = UserTeamManager.builder()
                        .user(creator)
                        .team(team)
                        .role(UserRole.LEADER)
                        .build();

        userteamRepository.save(utm);

        String invitationLink = "http://localhost:8080/icey/invitation/"+team.getInvitation();
        long days = Duration.between(LocalDateTime.now(), team.getExpiration()).toDays();
        String dDay = "D-" + days;

        return new TeamResponse(
                team.getTeamId(),
                team.getTeamName(),
                team.getMemberNum(),
                invitationLink,
                dDay
        );
    }

    public List<TeamResponse> getTeamsByUser(User user){ // 여기서 응답방식 수정 필요
        List<UserTeamManager> userTeams = userteamRepository.findByUser(user);

        return userTeams.stream()
                .map(utm -> {
                    Team team = utm.getTeam();
                    long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), team.getExpiration().toLocalDate());
                    String dDay = "D-" + daysLeft;

                    return new TeamResponse(
                            team.getTeamId(),
                            team.getTeamName(),
                            team.getMemberNum(),
                            team.getInvitation(),
                            dDay
                    );
                })
                .collect(Collectors.toList());

    }

    public InvitationResponse getInvitation(User user, Long teamId){
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Team을 찾지 못했습니다"));

        String invitationLink = "http://localhost:8080/icey/invitation/"+team.getInvitation();

        return new InvitationResponse(
                team.getTeamId(),
                team.getTeamName(),
                invitationLink

        );
    }

    public UserTeamJoinResponse joinTeamByInvitation(User user, String invitationToken) {
        Team team = teamRepository.findByInvitation(invitationToken)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "유효하지 않은 초대 링크입니다."));

        if (userteamRepository.existsByUserAndTeam(user, team)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 팀에 가입되어 있습니다.");
        }

        UserTeamManager relation = UserTeamManager.builder()
                .user(user)
                .team(team)
                .role(UserRole.MEMBER)
                .build();
        userteamRepository.save(relation);

        return new UserTeamJoinResponse(
                team.getTeamId(),
                team.getTeamName(),
                user.getId(),
                user.getUserName(),
                LocalDateTime.now().toString() // 필요시 formatter 적용
        );
    }

    @Transactional(readOnly = true)
    public TeamDetailResponse getTeamDetail(Long teamId, User user){
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 팀을 찾을 수 없습니다."));

        List<TeamMember> members = team.getMembers().stream()
                .map(utm -> new TeamMember(
                        utm.getUser().getId(),
                        utm.getUser().getUserName(),
                        utm.getRole()
                ))
                .collect(Collectors.toList());

        long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), team.getExpiration().toLocalDate());
        String dDay ="D-" + daysLeft;

        return new TeamDetailResponse(
                team.getTeamId(),
                team.getTeamName(),
                team.getMemberNum(),
                dDay,
                members

        );
    }



}
