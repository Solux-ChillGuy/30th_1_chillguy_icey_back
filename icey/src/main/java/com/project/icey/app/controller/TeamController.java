package com.project.icey.app.controller;

import com.project.icey.app.domain.Team;
import com.project.icey.app.domain.User;
import com.project.icey.app.domain.UserTeamManager;
import com.project.icey.app.dto.CreateTeamRequest;
import com.project.icey.app.dto.CustomUserDetails;
import com.project.icey.app.dto.TeamResponse;
import com.project.icey.app.repository.UserRepository;
import com.project.icey.app.repository.UserTeamRepository;
import com.project.icey.app.service.TeamService;
import com.project.icey.global.security.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/teams")
public class TeamController {

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

    // 팀 초대 링크 조회


    // 팀별 상세 기본정보 조회 + 멤버 목록도 포함할까?

    // 팀 폭파

    // 팀 탈퇴
}
