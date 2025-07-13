package com.project.icey.app.controller;

import com.project.icey.app.domain.User;
import com.project.icey.app.dto.CustomUserDetails;
import com.project.icey.app.dto.ScheduleCreateRequest;
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
}
