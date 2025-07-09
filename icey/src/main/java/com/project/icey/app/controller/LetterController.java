package com.project.icey.app.controller;

import com.project.icey.app.domain.User;
import com.project.icey.app.dto.CustomUserDetails;
import com.project.icey.app.dto.LetterDetailResponse;
import com.project.icey.app.dto.LetterSendRequest;
import com.project.icey.app.dto.WriteInfoResponse;
import com.project.icey.app.service.LetterService;
import com.project.icey.global.dto.ApiResponseTemplete;
import com.project.icey.global.exception.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class LetterController {

    private final LetterService letterService;

    //쪽지 작성화면 조회
    @GetMapping("/teams/{teamId}/cards/{cardId}/letters")
    public ResponseEntity<ApiResponseTemplete<WriteInfoResponse>> getWriteInfo(@PathVariable Long teamId,
                                          @PathVariable("cardId") Long receiverCardId,
                                          @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        WriteInfoResponse response = letterService.getWriteInfo(teamId, receiverCardId, user.getId());

        return ApiResponseTemplete.success(SuccessCode.LETTER_WRITE_INFO_SUCCESS, response);
    }

    //쪽지 보내기
    @PostMapping("/teams/{teamId}/cards/{cardId}/letters")
    public ResponseEntity<ApiResponseTemplete<Void>> sendLetter(@PathVariable Long teamId, @PathVariable("cardId") Long receiverCardId,
                                           @RequestBody LetterSendRequest request,
                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long currentUserId = userDetails.getUser().getId();

        letterService.sendLetter(
                teamId,
                receiverCardId,
                currentUserId,
                request.getContent()
        );

        return ApiResponseTemplete.success(SuccessCode.LETTER_SEND_SUCCESS, null);
    }

    //쪽지 상세 조회
    @GetMapping("/teams/{teamId}/letters/{letterId}")
    public LetterDetailResponse getLetterDetail(@PathVariable Long teamId, @PathVariable Long letterId, @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long currentUserId = userDetails.getUser().getId();

        return letterService.readLetter(teamId, letterId, currentUserId);
    }

}
