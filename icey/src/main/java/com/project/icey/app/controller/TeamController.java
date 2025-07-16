package com.project.icey.app.controller;

import com.project.icey.app.domain.Team;
import com.project.icey.app.domain.User;
import com.project.icey.app.domain.UserRole;
import com.project.icey.app.domain.UserTeamManager;
import com.project.icey.app.dto.*;
import com.project.icey.app.repository.TeamRepository;
import com.project.icey.app.repository.UserTeamRepository;
import com.project.icey.app.service.TeamService;

import com.project.icey.global.dto.ApiResponseTemplete;
import com.project.icey.global.exception.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/teams")
public class TeamController {


    private final TeamRepository teamRepository;
    private final TeamService teamService;
    private final UserTeamRepository userteamRepository;

    // 팀 생성
    @PostMapping("")
    public ResponseEntity<?> createTeam(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                   @RequestBody CreateTeamRequest createTeamRequest) {
        User user = userDetails.getUser();
        TeamResponse response = teamService.createTeam(createTeamRequest, user);

        return ApiResponseTemplete.success(SuccessCode.CREATE_TEAM_SUCCESS, response);
    }

    // 유저별 팀 목록 조회 + 여기서 user가 리더인지 아닌지 추가로 필요할 듯 -> 필요없어짐 + 현재 있는 팀이 무엇인지에
    @GetMapping("")
    public ResponseEntity<?>  getTeam(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        List<TeamResponse> teams = teamService.getTeamsByUser(user);
        return ApiResponseTemplete.success(SuccessCode.GET_TEAM_LIST_SUCCESS, teams);

    }

    //팀별 초대링크 조회 - 에러처리음 후순위
    @GetMapping("/{teamId}/invitation")
    public ResponseEntity<?> checkInvitation(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                              @PathVariable("teamId") Long teamId) {

        User user = userDetails.getUser();
        InvitationResponse response = teamService.getInvitation(user, teamId);

        return ApiResponseTemplete.success(SuccessCode.GET_INVITATION_SUCCESS, response);
    }

    //초대 링크 수락
    @PostMapping("/invitation/{invitationToken}")// 예외처리 추가해야함. 본인이 다시 중복가입을 시도하는 경우의 상태코드가 이상함(완료)
    public ResponseEntity<?> joinTeam(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                      @PathVariable String invitationToken){
        User user = userDetails.getUser();
        teamService.joinTeamByInvitation(user, invitationToken);
        return ApiResponseTemplete.success(SuccessCode.JOIN_TEAM_SUCCESS, null);
    }
    //초대링크를 통한 초대장(팀 정보) 조회
    @GetMapping("/invitation/{invitationToken}")
    public ResponseEntity<?> getTeamInfoByInvitationToken(@PathVariable String invitationToken) {
        InvitationTeamInfoResponse response = teamService.getTeamInfoByInvitation(invitationToken);
        return ApiResponseTemplete.success(SuccessCode.GET_TEAM_INFO_BY_INVITATION_SUCCESS, response);
    }


    // 팀별 상세 정보 조회
    @GetMapping("/{teamId}")
    public  ResponseEntity<?> getTeamDetail(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                            @PathVariable Long teamId) {
        User user = userDetails.getUser();
        TeamDetailResponse response = teamService.getTeamDetail(teamId, user);
        return ApiResponseTemplete.success(SuccessCode.GET_TEAM_DETAIL_SUCCESS, response);
    }

    // 팀 폭파
    /*@DeleteMapping("/{teamId}")
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

        teamRepository.delete(team);
        return ResponseEntity.noContent().build();
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
    }*/
    //안녕 나의 코드들...

}
