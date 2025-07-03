package com.project.icey.app.controller;

import com.project.icey.app.domain.Team;
import com.project.icey.app.domain.User;
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

        return ResponseEntity.ok(response);
    }

    // 유저별 팀 목록 조회
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
    @PostMapping("/invitation/{invitationToken}")
    public ResponseEntity<UserTeamJoinResponse> joinTeam(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                      @PathVariable String invitationToken){

        User user = userDetails.getUser();

       UserTeamJoinResponse response = teamService.joinTeamByInvitation(user, invitationToken);

        return ResponseEntity.ok(response);
    }



    // 팀별 상세 기본정보 조회

    // 팀 폭파


    // 팀 탈퇴

}
