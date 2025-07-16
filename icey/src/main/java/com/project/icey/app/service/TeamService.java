package com.project.icey.app.service;

import com.project.icey.app.domain.*;
import com.project.icey.app.dto.*;
import com.project.icey.app.repository.*;
import com.project.icey.global.exception.ErrorCode;
import com.project.icey.global.exception.model.CoreApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository teamRepository;
    private final UserTeamRepository userteamRepository;
    private final CardRepository cardRepository;
    private final ScheduleRepository scheduleRepository;

    public TeamResponse createTeam(CreateTeamRequest request, User creator){
        Team team = Team.builder() //
                .teamName(request.getTeamName())
                .invitation(UUID.randomUUID().toString())
                .build();

        teamRepository.save(team);

        UserTeamManager utm = UserTeamManager.builder()
                .user(creator)
                .team(team)
                .role(UserRole.LEADER)
                .build();

        userteamRepository.save(utm);

        //int memberCnt = userteamRepository.countByTeam(team);

        String invitationLink = "http://localhost:8080/icey/invitation/"+team.getInvitation();
        long days = Duration.between(LocalDateTime.now(), team.getExpiration()).toDays();
        String dDay = "D-" + days;

        return new TeamResponse(
                team.getTeamId(),
                team.getTeamName(),
                dDay
        );
    }

    public List<TeamResponse> getTeamsByUser(User user){
        List<UserTeamManager> userTeams = userteamRepository.findByUser(user);

        return userTeams.stream()
                .map(utm -> {
                    Team team = utm.getTeam();

                    //int memberCnt = userteamRepository.countByTeam(team);

                    long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), team.getExpiration().toLocalDate());
                    String dDay = "D-" + daysLeft;

                    return new TeamResponse(
                            team.getTeamId(),
                            team.getTeamName(),
                            dDay

                    );
                })
                .collect(Collectors.toList());

    }

    public InvitationResponse getInvitation(User user, Long teamId){
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new CoreApiException(ErrorCode.TEAM_NOT_FOUND));

        String invitationLink = "http://localhost:8080/icey/invitation/"+team.getInvitation();

        return new InvitationResponse(
                team.getTeamId(),
                team.getTeamName(),
                invitationLink

        );
    }

    public void joinTeamByInvitation(User user, String invitationToken) {
        Team team = teamRepository.findByInvitation(invitationToken)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "유효하지 않은 초대 링크입니다."));
        int memberCnt = userteamRepository.countByTeam(team);

        if(memberCnt >= 10) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "팀 인원 수가 최대 10명을 초과하여 더 이상 가입할 수 없습니다.");
        }

        if (userteamRepository.existsByUserAndTeam(user, team)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 팀에 가입되어 있습니다."); //409 Conflict로 변경
        }



        UserTeamManager relation = UserTeamManager.builder()
                .user(user)
                .team(team)
                .role(UserRole.MEMBER)
                .build();
        userteamRepository.save(relation);
    }

    @Transactional(readOnly = true)
    public TeamDetailResponse getTeamDetail(Long teamId, User user){
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 팀을 찾을 수 없습니다."));

        int memberCnt = userteamRepository.countByTeam(team);

        /*List<TeamMember> members = team.getMembers().stream()
                .map(utm -> new TeamMember(
                        utm.getUser().getId(),
                        utm.getUser().getUserName(),
                        utm.getRole()
                ))
                .collect(Collectors.toList());*/

        long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), team.getExpiration().toLocalDate());
        String dDay ="D-" + daysLeft;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM.dd");
        String currentDate = LocalDate.now().format(formatter);

        UserTeamManager userTeamManager = userteamRepository.findByUserAndTeam(user, team)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "팀의 멤버가 아닙니다."));

        UserRole role = userTeamManager.getRole();

        //정해진 약속잡기가 있는지 조회
        boolean hasSchedule = scheduleRepository.existsByTeam_TeamId(teamId);
        String confirmedDate = null;
        if (hasSchedule) {
            Schedule schedule = scheduleRepository.findByTeam_TeamId(teamId)
                    .orElse(null);
            if (schedule != null && schedule.isConfirmed() && schedule.getConfirmedDate() != null) {
                confirmedDate = schedule.getConfirmedDate().toString();
            }
        }


        return new TeamDetailResponse(
                team.getTeamId(),
                team.getTeamName(),
                memberCnt,
                currentDate,
                dDay,
                role.name(),
                hasSchedule,
                confirmedDate
        );

    }

    @Transactional(readOnly = true)
    public InvitationTeamInfoResponse getTeamInfoByInvitation(String invitationToken){
        Team team = teamRepository.findByInvitation(invitationToken)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "유효하지 않은 초대 링크입니다."));

        //int memberCnt = userteamRepository.countByTeam(team);
        //2025-07-15 memberCnt가 아닌 리더의 이름을 반환하도록 수정
        UserTeamManager leaderUtm = team.getMembers().stream()
                .filter(utm -> utm.getRole() == UserRole.LEADER)
                .findFirst()
                .orElseThrow(() ->  new IllegalArgumentException("팀에 리더가 존재하지 않습니다."));
        User leader = leaderUtm.getUser();
        Card leaderCard = cardRepository.findByUserAndTeam(leader, team)
                .orElseThrow(() -> new IllegalArgumentException("리더의 명함이 존재하지 않습니다."));

        String leaderNickname = leaderCard.getNickname();


        return new InvitationTeamInfoResponse(
                team.getTeamName(),
                leaderNickname
        );
    }



}