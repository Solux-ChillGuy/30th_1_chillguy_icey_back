package com.project.icey.app.controller;

import com.project.icey.app.domain.User;
import com.project.icey.app.dto.CustomUserDetails;
import com.project.icey.app.dto.WriteInfoResponse;
import com.project.icey.app.service.LetterService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LetterController {

    private final LetterService letterService;


    @GetMapping("/teams/{team_id}/cards/{card_id}/letters/write-info")
    public WriteInfoResponse getWriteInfo(@PathVariable("team_id") Long teamId,
                                          @PathVariable("card_id") Long receiverCardId,
                                          @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();

        return letterService.getWriteInfo(teamId, receiverCardId, user.getId());
    }
}
