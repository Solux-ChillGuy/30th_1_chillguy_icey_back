package com.project.icey.app.controller;

import com.project.icey.app.domain.Team;
import com.project.icey.app.domain.User;
import com.project.icey.app.domain.UserRole;
import com.project.icey.app.domain.UserTeamManager;
import com.project.icey.app.dto.*;
import com.project.icey.app.repository.TeamRepository;
import com.project.icey.app.repository.UserRepository;
import com.project.icey.app.repository.UserTeamRepository;
import com.project.icey.app.service.TeamService;
import com.project.icey.global.security.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/teams")
public class TeamController {


    private final TeamRepository teamRepository;
    private final TeamService teamService;
    private final UserTeamRepository userteamRepository;

    // 팀 생성
    @PostMapping("")
    public ResponseEntity<TeamResponse> createTeam(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                   @RequestBody CreateTeamRequest createTeamRequest) {
        User user = userDetails.getUser();
        TeamResponse response = teamService.createTeam(createTeamRequest, user);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 유저별 팀 목록 조회 + 여기서 user가 리더인지 아닌지 추가로 필요할 듯
    @GetMapping("")
    public ResponseEntity<List<TeamResponse>> getTeam(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        List<TeamResponse> teams = teamService.getTeamsByUser(user);
        return ResponseEntity.ok(teams);

    }

    //팀별 초대링크 조회 - 에러처리음 후순위
    @GetMapping("/{teamId}/invitation")
    public ResponseEntity<InvitationResponse> checkInvitation(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                              @PathVariable("teamId") Long teamId) {

        User user = userDetails.getUser();

        InvitationResponse response = teamService.getInvitation(user, teamId);

        return ResponseEntity.ok(response);
    }

    //초대 링크 수락
    @PostMapping("/invitation/{invitationToken}")// 예외처리 추가해야함. 본인이 다시 중복가입을 시도하는 경우의 상태코드가 이상함
    public ResponseEntity<UserTeamJoinResponse> joinTeam(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                      @PathVariable String invitationToken){

        User user = userDetails.getUser();

       UserTeamJoinResponse response = teamService.joinTeamByInvitation(user, invitationToken);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    // 팀별 상세 기본정보 조회
    @GetMapping("/{teamId}")
    public ResponseEntity<TeamDetailResponse> getTeamDetail(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                            @PathVariable Long teamId) {
        User user = userDetails.getUser();
        TeamDetailResponse response = teamService.getTeamDetail(teamId, user);
        return ResponseEntity.ok(response);
    }

    // 팀 폭파
    @DeleteMapping("/{teamId}")
    public ResponseEntity<String> DeleteTeam(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                   @PathVariable Long teamId){
        User user = userDetails.getUser();
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 팀을 찾을 수 없습니다."));

        UserTeamManager utm = userteamRepository.findByUserAndTeam(user, team)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "팀에 소속되어 있지 않습니다."));

        if (utm.getRole() != UserRole.LEADER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "팀장만 팀을 삭제할 수 있습니다.");
        }

        teamRepository.delete(team); // Cascade로 members도 삭제됨
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    // 팀 탈퇴
    @DeleteMapping("/{teamId}/leave")
    public ResponseEntity<String> leaveTeam(@AuthenticationPrincipal CustomUserDetails userDetails,
                                            @PathVariable Long teamId) {
        User user = userDetails.getUser();
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 팀을 찾을 수 없습니다."));

        UserTeamManager utm = userteamRepository.findByUserAndTeam(user, team)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "이 팀에 속해있지 않습니다."));

        if (utm.getRole() == UserRole.LEADER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "팀장은 팀을 탈퇴할 수 없습니다. 팀 삭제만 가능합니다.");
        }

        userteamRepository.delete(utm);
        return ResponseEntity.noContent().build();
    }

}
