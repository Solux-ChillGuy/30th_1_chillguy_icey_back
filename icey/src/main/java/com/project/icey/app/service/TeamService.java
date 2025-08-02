package com.project.icey.app.service;

import com.project.icey.app.domain.*;
import com.project.icey.app.dto.*;
import com.project.icey.app.repository.*;
import com.project.icey.global.dto.ApiResponseTemplete;
import com.project.icey.global.exception.AlreadyJoinedException;
import com.project.icey.global.exception.ErrorCode;
import com.project.icey.global.exception.model.CoreApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository teamRepository;
    private final UserTeamRepository userteamRepository;
    private final CardRepository cardRepository;
    private final ScheduleRepository scheduleRepository;
    private final CardService cardService;
    private final ScheduleVoteRepository scheduleVoteRepository;
    private final MemoService memoService; //첫 메모 추가용


    @Value("${app.frontEndBaseUrl}")
    private String baseUrl;

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

        //사용자가 팀 생성과 동시에 팀 관련 명함이 생성될 수 있도록
        cardService.ensureMyCard(team.getTeamId(), creator);


        //팀 생성과 동시에 첫 메모를 추가(팀장이 만든 걸로 처리)
        MemoRequest welcomeMemo = new MemoRequest();
        welcomeMemo.setContent(
                "팀페이지에서 팀원에게 쪽지를 보내보세요!\n\n" +
                        "내 명함을 커스텀 해보세요!\n\n" +
                        "1. 기분 좋아지는 말 쪽지하기\n" +
                        "2. 만났을 때 나를 알아볼 수 있는 쪽지 보내기"
        );
        memoService.create(team.getTeamId(), welcomeMemo, creator);


        //int memberCnt = userteamRepository.countByTeam(team);
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

        String invitationLink = baseUrl + "/invitation/"+team.getInvitation();

        return new InvitationResponse(
                team.getTeamId(),
                team.getTeamName(),
                invitationLink

        );
    }

    public Long joinTeamByInvitation(User user, String invitationToken) {
        Team team = teamRepository.findByInvitation(invitationToken)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "유효하지 않은 초대 링크입니다."));
        int memberCnt = userteamRepository.countByTeam(team);

        if(memberCnt >= 10) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "팀 인원 수가 최대 10명을 초과하여 더 이상 가입할 수 없습니다.");
        }

        if (userteamRepository.existsByUserAndTeam(user, team)) {
            throw new AlreadyJoinedException(team.getTeamId()); //409 Conflict로 변경
        }

        UserTeamManager relation = UserTeamManager.builder()
                .user(user)
                .team(team)
                .role(UserRole.MEMBER)
                .build();
        userteamRepository.save(relation);

        //팀에 가입됨과 동시에 명함 연결
        cardService.ensureMyCard(team.getTeamId(), user);

        return team.getTeamId();
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
        boolean isAllVoted = false;
        int confirmedHour = 0;
        if (hasSchedule) {
            Schedule schedule = scheduleRepository.findByTeam_TeamId(teamId)
                    .orElse(null);

            // 팀 전체가 투표했는지 계산
            Long votedCount = scheduleVoteRepository.countDistinctVotersByScheduleId(schedule.getScheduleId());
            int totalMembers = team.getMembers().size();
            isAllVoted = (votedCount != null && votedCount == totalMembers);

            if (schedule != null && schedule.isConfirmed() && schedule.getConfirmedDate() != null) {
                confirmedDate = schedule.getConfirmedDate().toString();
                confirmedHour = schedule.getConfirmedHour();
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
                confirmedDate,
                team.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                isAllVoted,
                confirmedHour
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
        
        //만약 팀장이 아직 명함을 안만들었다면.. ver1 아예 에러 뜨게 처리 안좋은것 같음
        /*Card leaderCard = cardRepository.findByUserAndTeam(leader, team)
                .orElseThrow(() -> new IllegalArgumentException("리더의 명함이 존재하지 않습니다."));

        String leaderNickname = leaderCard.getNickname();*/
        
        //미등록은 닉네임 미등록 사용자로 처리하는거 어떤지 물어보기
        String leaderNickname = cardRepository.findByUserAndTeam(leader, team)
                .map(Card::getNickname)
                .orElse("닉네임 미등록 사용자");


        return new InvitationTeamInfoResponse(
                team.getTeamId(),
                team.getTeamName(),
                leaderNickname
        );
    }



}