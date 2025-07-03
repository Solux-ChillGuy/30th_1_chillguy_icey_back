package com.project.icey.app.controller;
import com.project.icey.app.domain.Team;
import com.project.icey.app.domain.User;
import com.project.icey.app.dto.CustomUserDetails;
import com.project.icey.app.dto.InvitationResponse;
import com.project.icey.app.dto.TeamResponse;
import com.project.icey.app.repository.TeamRepository;
import com.project.icey.app.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/invitation")
public class InvitationController {

    private final TeamRepository teamRepository;
    private final TeamService teamService;

    @GetMapping("/{invitationCode}")
    public ResponseEntity<InvitationResponse> checkInvitation(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                              @PathVariable String invitationCode) {

        User user = userDetails.getUser();
        Team team = teamRepository.findByInvitation(invitationCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "초대 코드가 유효하지 않습니다."));

        InvitationResponse response = teamService.getInvitation(user, team.getTeamId());

        return ResponseEntity.ok(response);
    }

}
