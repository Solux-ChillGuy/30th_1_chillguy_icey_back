package com.project.icey.app.controller;

import com.project.icey.app.dto.*;
import com.project.icey.app.service.ScheduleService;
import com.project.icey.global.dto.ApiResponseTemplete;
import com.project.icey.global.exception.SuccessCode;
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

        ScheduleVoteCombinedResponse response = scheduleService.createSchedule(teamId, userDetails.getUser().getId(), request);
        return ApiResponseTemplete.success(SuccessCode.CREATED_SCHEDULE, response);    }

    //약속 잡기 투표
    @PostMapping("/{teamId}/schedule/votes")
    public ResponseEntity<?> submitVote(@PathVariable Long teamId,
                                        @AuthenticationPrincipal CustomUserDetails userDetails,
                                        @RequestBody ScheduleVoteRequest request) {
        ScheduleVoteCombinedResponse response = scheduleService.submitVote(teamId, userDetails.getUser().getId(), request);
        return ApiResponseTemplete.success(SuccessCode.CREATED_VOTE, response);
    }

    //약속 잡기 본인이 투표한 것 조회
    @GetMapping("/{teamId}/schedule/votes-mine")
    public ResponseEntity<?> getMyVotes(@PathVariable Long teamId,
                                                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        ScheduleVoteResponse response = scheduleService.getMyVotes(teamId, userDetails.getUser().getId());
        return ApiResponseTemplete.success(SuccessCode.GET_MY_VOTE, response);
    }

    //약속 잡기 투표 현황 조회
    @GetMapping("/{teamId}/schedule/votes")
    public ResponseEntity<?> getVoteSummary(@PathVariable Long teamId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        ScheduleVoteSummaryResponse response = scheduleService.getVoteSummary(teamId);
        return ApiResponseTemplete.success(SuccessCode.GET_VOTE_SUMMARY, response);
    }

    //본인 투표 + 투표 현황 종합 조회 ver
    @GetMapping("/{teamId}/schedule/votes-summary")
    public ResponseEntity<?> getCombinedVoteSummary(@PathVariable Long teamId,
                                                    @AuthenticationPrincipal CustomUserDetails userDetails) {
        ScheduleVoteCombinedResponse response = scheduleService.getCombinedVoteResult(teamId, userDetails.getUser().getId());
        return ApiResponseTemplete.success(SuccessCode.GET_COMBINED_VOTE_SUMMARY, response);
    }

    @GetMapping("/{teamId}/schedule/best-candidate")
    public ResponseEntity<?> getBestCandidateTimes(@PathVariable Long teamId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        MajorityTimeResponse response = scheduleService.getMajorityAvailableTimeForAllDates(teamId);
        return ApiResponseTemplete.success(SuccessCode.GET_BEST_CANDIDATE, response);
    }
    @PostMapping("/{teamId}/schedule/confirm")
    public ResponseEntity<?> confirmSchedule(@PathVariable Long teamId,
                                             @AuthenticationPrincipal CustomUserDetails userDetails,
                                             @RequestBody ConfirmScheduleRequest request) {
        scheduleService.confirmSchedule(teamId, userDetails.getUser().getId(), request);
        return ApiResponseTemplete.success(SuccessCode.CONFIRMED_SCHEDULE, null);
    }

}
