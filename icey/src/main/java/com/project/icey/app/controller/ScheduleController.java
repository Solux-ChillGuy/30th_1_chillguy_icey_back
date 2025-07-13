package com.project.icey.app.controller;

import com.project.icey.app.dto.CustomUserDetails;
import com.project.icey.app.dto.ScheduleCreateRequest;
import com.project.icey.app.dto.ScheduleVoteDTO;
import com.project.icey.app.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/teams")
public class ScheduleController {

    private final ScheduleService scheduleService;

    //약속 잡기 생성
    @PostMapping("/{teamId}/schedule")
    public ResponseEntity<?> createSchedule(@PathVariable Long teamId,
                                            @RequestBody ScheduleCreateRequest request,
                                            @AuthenticationPrincipal CustomUserDetails userDetails){

        scheduleService.createSchedule(teamId, userDetails.getUser().getId(), request);
        return ResponseEntity.ok("스케줄 투표가 생성되었습니다.");
    }

    //약속 잡기 투표
    @PostMapping("/{teamId}/schedule/votes")
    public ResponseEntity<?> submitVote(@PathVariable Long teamId,
                                        @AuthenticationPrincipal CustomUserDetails userDetails,
                                        @RequestBody ScheduleVoteDTO request) {
        scheduleService.submitVote(teamId, userDetails.getUser().getId(), request);
        return ResponseEntity.ok("투표가 저장되었습니다.");
    }

    //약속 잡기 본인이 투표한 것 조회
    @GetMapping("/{teamId}/schedule/votes-mine")
    public ResponseEntity<?> getMyVotes(@PathVariable Long teamId,
                                                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        ScheduleVoteDTO response = scheduleService.getMyVotes(teamId, userDetails.getUser().getId());
        return ResponseEntity.ok(response);
    }

}
