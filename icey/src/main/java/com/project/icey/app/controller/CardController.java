package com.project.icey.app.controller;

import com.project.icey.app.dto.CardRequest;
import com.project.icey.app.dto.CardResponse;
import com.project.icey.app.dto.CustomUserDetails;
import com.project.icey.app.dto.SimpleTeamInfo;
import com.project.icey.app.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    // 1. 내 명함 템플릿 관련

    /* 내 템플릿 목록 조회 (팀에 입장할 때와는 다름. 팀 입장 기본 템플릿은 팀 명함 파트에 있다 */
    @GetMapping
    public List<CardResponse> list(@AuthenticationPrincipal CustomUserDetails principal) {
        return cardService.listTemplates(principal.getUser().getId());
    }

    /* 템플릿 생성 */
    @PostMapping(params = "!teamId")
    public CardResponse create(@Valid @RequestBody CardRequest req,
                               @AuthenticationPrincipal CustomUserDetails principal) {
        return cardService.createTemplate(principal.getUser(), req);
    }

    /* 템플릿 수정 */
    @PatchMapping("/{id}")
    public CardResponse update(@PathVariable Long id,
                               @Valid @RequestBody CardRequest req,
                               @AuthenticationPrincipal CustomUserDetails principal) {
        return cardService.update(id, principal.getUser().getId(), req);
    }

    /* 템플릿 삭제 */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id,
                       @AuthenticationPrincipal CustomUserDetails principal) {
        cardService.delete(id, principal.getUser().getId());
    }

    /*이 템플릿을 사용하는 팀 목록 조회*/
    @GetMapping("/{templateId}/used-teams")
    public List<SimpleTeamInfo> getUsedTeamIds(
            @PathVariable Long templateId,
            @AuthenticationPrincipal CustomUserDetails principal  // 로그인 유저 참고
    ) {
        Long userId = principal.getUser().getId();
        return cardService.getMyTeamInfosUsingTemplate(templateId, userId);
    }








    // 2. 팀 명함 관련

    /* 팀 명함 목록 조회 (내 명함 식별 포함) */
    @GetMapping("/teams/{teamId}/cards")
    public List<CardResponse> teamCards(@PathVariable Long teamId) {
        return cardService.listTeamCards(teamId);
    }

    /* 팀 입장 시 내 명함 자동 생성/템플릿 존재할 시 기존 것 사용을 통해 보장 */
    @PostMapping("/teams/{teamId}/cards/init")
    public CardResponse ensureMyCard(@PathVariable Long teamId,
                                     @AuthenticationPrincipal CustomUserDetails principal) {
        return cardService.ensureMyCard(teamId, principal.getUser());
    }

    /* 팀에서 내 명함 교체 (템플릿 적용) */
    @PutMapping("/teams/{teamId}/cards/my-card")
    public CardResponse changeMyCard(@PathVariable Long teamId,
                                     @RequestParam Long templateId,
                                     @AuthenticationPrincipal CustomUserDetails principal) {
        return cardService.applyTemplate(teamId, templateId, principal.getUser());
    }

    // 내 팀카드(내가 이 팀에서 쓰는 카드)만 반환하는 API
    @GetMapping("/teams/{teamId}/cards/my")
    public CardResponse getMyTeamCard(
            @PathVariable Long teamId,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        return cardService.getMyTeamCard(teamId, principal.getUser().getId());
    }

}
