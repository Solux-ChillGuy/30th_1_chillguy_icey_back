package com.project.icey.app.controller;

import com.project.icey.app.domain.BalanceGame;
import com.project.icey.app.domain.NotificationType;
import com.project.icey.app.dto.BalanceGameDto;
import com.project.icey.app.dto.BalanceGameResultDto;
import com.project.icey.app.dto.BalanceGameVoteRequest;
import com.project.icey.app.dto.CustomUserDetails;
import com.project.icey.app.service.BalanceGameService;
import com.project.icey.app.service.NotificationService;
import com.project.icey.global.dto.ApiResponseTemplete;
import com.project.icey.global.exception.ErrorCode;
import com.project.icey.global.exception.SuccessCode;
import com.project.icey.global.exception.model.CoreApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/teams/{teamId}/balance-game")
public class BalanceGameController {

    private final BalanceGameService balanceGameService;
    private final NotificationService notificationService;

    // 전체 조회
    @GetMapping
    public ResponseEntity<ApiResponseTemplete<List<BalanceGameResultDto>>> getAllBalanceGames(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long teamId
    ) {
        List<BalanceGameResultDto> results = balanceGameService.getAllGameResultsByTeam(teamId, userDetails.getUser());
        return ApiResponseTemplete.success(SuccessCode.GET_POST_SUCCESS, results);
    }


    // 게임 생성
    @PostMapping("/generate")
    public ResponseEntity<ApiResponseTemplete<BalanceGameDto>> createBalanceGame(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long teamId
    ) {
        BalanceGame game = balanceGameService.createBalanceGame(teamId);
        BalanceGameDto dto = BalanceGameDto.from(game);

        String teamName = game.getTeam().getTeamName();

        Long userId = userDetails.getUser().getId();
        notificationService.sendNotification(
                userId,
                NotificationType.BALANCE_GAME_CREATED,
                teamName
        );

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
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long teamId,
            @PathVariable Long gameId
    ) {
        BalanceGameResultDto result = balanceGameService.getResult(gameId, userDetails.getUser());
        return ApiResponseTemplete.success(SuccessCode.GET_POST_SUCCESS, result);
    }

    @DeleteMapping("/{gameId}/delete")
    public ResponseEntity<?> deleteBalanceGame(
            @PathVariable Long teamId,
            @PathVariable Long gameId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        balanceGameService.deleteBalanceGame(gameId);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
