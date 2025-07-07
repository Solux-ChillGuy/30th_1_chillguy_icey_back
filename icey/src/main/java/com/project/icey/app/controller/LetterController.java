package com.project.icey.app.controller;

import com.project.icey.app.domain.User;
import com.project.icey.app.dto.CustomUserDetails;
import com.project.icey.app.dto.LetterSendRequest;
import com.project.icey.app.dto.WriteInfoResponse;
import com.project.icey.app.service.LetterService;
import com.project.icey.global.dto.ApiResponseTemplete;
import com.project.icey.global.exception.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class LetterController {

    private final LetterService letterService;

    //쪽지 작성화면 조회
    @GetMapping("/teams/{team_id}/cards/{card_id}/letters")
    public ResponseEntity<ApiResponseTemplete<WriteInfoResponse>> getWriteInfo(@PathVariable("team_id") Long teamId,
                                          @PathVariable("card_id") Long receiverCardId,
                                          @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        WriteInfoResponse response = letterService.getWriteInfo(teamId, receiverCardId, user.getId());

        return ApiResponseTemplete.success(SuccessCode.LETTER_WRITE_INFO_SUCCESS, response);
    }

    //쪽지 보내기
    @PostMapping("/teams/{team_id}/cards/{card_id}/letters")
    public ResponseEntity<ApiResponseTemplete<Void>> sendLetter(@PathVariable("team_id") Long teamId,
                                           @RequestBody LetterSendRequest request,
                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long currentUserId = userDetails.getUser().getId();

        letterService.sendLetter(
                teamId,
                request.getReceiverCardId(),
                currentUserId,
                request.getContent()
        );

        return ApiResponseTemplete.success(SuccessCode.LETTER_SEND_SUCCESS, null);
    }
}
