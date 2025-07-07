package com.project.icey.app.controller;

import com.project.icey.app.domain.BalanceGame;
import com.project.icey.app.dto.BalanceGameResultDto;
import com.project.icey.app.dto.BalanceGameVoteRequest;
import com.project.icey.app.dto.CustomUserDetails;
import com.project.icey.app.service.BalanceGameService;
import com.project.icey.global.dto.ApiResponseTemplete;
import com.project.icey.global.exception.ErrorCode;
import com.project.icey.global.exception.SuccessCode;
import com.project.icey.global.exception.model.CoreApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/teams/{teamId}/balance-game")
public class BalanceGameController {

    private final BalanceGameService balanceGameGeneratorService;

    // 게임 생성
    @PostMapping("/generate")
    public ResponseEntity<ApiResponseTemplete<BalanceGame>> createBalanceGame(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        // 예: userDetails로 팀Id 찾는 로직 추가 가능
        BalanceGame game = balanceGameGeneratorService.createBalanceGame(null);
        return ApiResponseTemplete.success(SuccessCode.CREATE_POST_SUCCESS, game);
    }

    // 투표
    @PostMapping("/{gameId}/vote")
    public ResponseEntity<ApiResponseTemplete<Void>> vote(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long gameId,
            @RequestBody BalanceGameVoteRequest request
    ) {
        if (userDetails == null || userDetails.getUser() == null) {
            throw new CoreApiException(ErrorCode.UNAUTHORIZED_EXCEPTION);
        }
        balanceGameGeneratorService.vote(gameId, userDetails.getUser(), request.getSelectedOption());
        return ApiResponseTemplete.success(SuccessCode.CREATE_POST_SUCCESS, null);
    }

    // 결과 조회
    @GetMapping("/{gameId}/result")
    public ResponseEntity<ApiResponseTemplete<BalanceGameResultDto>> getResult(@PathVariable Long gameId) {
        BalanceGameResultDto result = balanceGameGeneratorService.getResult(gameId);
        return ApiResponseTemplete.success(SuccessCode.GET_POST_SUCCESS, result);
    }
}
