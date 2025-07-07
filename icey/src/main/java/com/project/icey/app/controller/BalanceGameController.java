package com.project.icey.app.controller;

import com.project.icey.app.domain.BalanceGame;
import com.project.icey.app.dto.BalanceGameDto;
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

    private final BalanceGameService balanceGameService;

    // 게임 생성
    // 반환 타입을 BalanceGameDto로 변경
    @PostMapping("/generate")
    public ResponseEntity<ApiResponseTemplete<BalanceGameDto>> createBalanceGame(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long teamId
    ) {
        BalanceGame game = balanceGameService.createBalanceGame(teamId);
        BalanceGameDto dto = BalanceGameDto.from(game);
        return ApiResponseTemplete.success(SuccessCode.CREATE_POST_SUCCESS, dto);
    }


    // 투표
    @PostMapping("/{gameId}/vote")
    public ResponseEntity<ApiResponseTemplete<Void>> vote(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long teamId,
            @PathVariable Long gameId,
            @RequestBody BalanceGameVoteRequest request
    ) {
        if (userDetails == null || userDetails.getUser() == null) {
            throw new CoreApiException(ErrorCode.UNAUTHORIZED_EXCEPTION);
        }
        balanceGameService.vote(gameId, userDetails.getUser(), request.getSelectedOption());
        return ApiResponseTemplete.success(SuccessCode.CREATE_POST_SUCCESS, null);
    }

    // 결과 조회
    @GetMapping("/{gameId}/result")
    public ResponseEntity<ApiResponseTemplete<BalanceGameResultDto>> getResult(
            @PathVariable Long teamId,
            @PathVariable Long gameId
    ) {
        BalanceGameResultDto result = balanceGameService.getResult(gameId);
        return ApiResponseTemplete.success(SuccessCode.GET_POST_SUCCESS, result);
    }
}
