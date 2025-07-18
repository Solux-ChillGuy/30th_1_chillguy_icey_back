package com.project.icey.app.controller;

import com.project.icey.app.dto.CardRequest;
import com.project.icey.app.dto.CardResponse;
import com.project.icey.app.dto.CustomUserDetails;
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

    /* GET /api/cards - 내 템플릿 목록 */
    @GetMapping
    public List<CardResponse> list(@AuthenticationPrincipal CustomUserDetails principal) { 
        return cardService.listTemplates(principal.getUser().getId());                    
    }

    /* POST /api/cards - 템플릿 생성 */
    @PostMapping(params = "!teamId")
    public CardResponse create(@Valid @RequestBody CardRequest req,
                               @AuthenticationPrincipal CustomUserDetails principal) {   
        return cardService.createTemplate(principal.getUser(), req);                      
    }

    /* PATCH /api/cards/{id} - 템플릿 수정 */
    @PatchMapping("/{id}")
    public CardResponse update(@PathVariable Long id,
                               @Valid @RequestBody CardRequest req,
                               @AuthenticationPrincipal CustomUserDetails principal) {   
        return cardService.update(id, principal.getUser().getId(), req);                 
    }

    /* DELETE /api/cards/{id} - 템플릿 삭제 */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id,
                       @AuthenticationPrincipal CustomUserDetails principal) {           
        cardService.delete(id, principal.getUser().getId());                              
    }

    /* GET /api/teams/{teamId}/cards - 팀 명함 목록 */  //여기 상의하기
    @GetMapping("/teams/{teamId}/cards")
    public List<CardResponse> teamCards(@PathVariable Long teamId) {
        return cardService.listTeamCards(teamId);
    }

    /* PUT /api/teams/{teamId}/cards/my-card?templateId=123 - 팀 명함 교체 */
    @PutMapping("/teams/{teamId}/cards/my-card")
    public CardResponse changeMyCard(@PathVariable Long teamId,
                                     @RequestParam Long templateId,
                                     @AuthenticationPrincipal CustomUserDetails principal) { 
        return cardService.applyTemplate(teamId, templateId, principal.getUser());           
    }


}