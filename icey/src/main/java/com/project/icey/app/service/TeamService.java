package com.project.icey.app.service;

import com.project.icey.app.domain.Team;
import com.project.icey.app.domain.User;
import com.project.icey.app.domain.UserRole;
import com.project.icey.app.domain.UserTeamManager;
import com.project.icey.app.dto.CreateTeamRequest;
import com.project.icey.app.dto.InvitationResponse;
import com.project.icey.app.dto.TeamResponse;
import com.project.icey.app.repository.TeamRepository;
import com.project.icey.app.repository.UserRepository;
import com.project.icey.app.repository.UserTeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository teamRepository;
    private final UserTeamRepository userteamRepository;

    public TeamResponse createTeam(CreateTeamRequest request, User creator){
        Team team = Team.builder() // ✅ builder 일관성 적용
                .teamName(request.getTeamName())
                .memberNum(request.getMemberNum())
                .invitation(UUID.randomUUID().toString())
                .build();

        teamRepository.save(team);

        UserTeamManager utm = UserTeamManager.builder()
                        .user(creator)
                        .team(team)
                        .role(UserRole.LEADER.name())
                        .build();

        userteamRepository.save(utm);

        return new TeamResponse(
                team.getTeamId(),
                team.getTeamName(),
                team.getMemberNum(),
                team.getInvitation()
        );
    }

    public List<TeamResponse> getTeamsByUser(User user){ // 여기서 응답방식 수정 필요
        List<UserTeamManager> userTeams = userteamRepository.findByUser(user);

        return userTeams.stream()
                .map(utm -> {
                    Team team = utm.getTeam();
                    return new TeamResponse(
                            team.getTeamId(),
                            team.getTeamName(),
                            team.getMemberNum(),
                            team.getInvitation()
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
}
