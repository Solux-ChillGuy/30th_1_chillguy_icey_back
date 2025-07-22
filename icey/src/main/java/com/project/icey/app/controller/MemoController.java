package com.project.icey.app.controller;

import com.project.icey.app.dto.MemoResponse;
import com.project.icey.app.dto.MemoRequest;
import com.project.icey.app.service.MemoService;
import com.project.icey.app.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/teams/{teamId}/memos")
@RequiredArgsConstructor
public class MemoController {

    private final MemoService memoService;

    @GetMapping
    public List<MemoResponse> list(@PathVariable Long teamId,
                                   @AuthenticationPrincipal CustomUserDetails p) {
        return memoService.list(teamId, p.getUser().getId());
    }

    @GetMapping("/{memoId}")
    public MemoResponse detail(@PathVariable Long teamId, @PathVariable Long memoId,
                               @AuthenticationPrincipal CustomUserDetails p) {
        return memoService.detail(teamId, memoId, p.getUser().getId());
    }

    @PostMapping
    public MemoResponse create(@PathVariable Long teamId,
                               @RequestBody @Valid MemoRequest req,
                               @AuthenticationPrincipal CustomUserDetails p) {
        return memoService.create(teamId, req, p.getUser());
    }

    @PutMapping("/{memoId}")
    public MemoResponse update(@PathVariable Long teamId, @PathVariable Long memoId,
                               @RequestBody @Valid MemoRequest req,
                               @AuthenticationPrincipal CustomUserDetails p) {
        return memoService.update(teamId, memoId, req, p.getUser().getId());
    }

    @DeleteMapping("/{memoId}")
    public void delete(@PathVariable Long teamId, @PathVariable Long memoId,
                       @AuthenticationPrincipal CustomUserDetails p) {
        memoService.delete(teamId, memoId, p.getUser().getId());
    }

    @PostMapping("/{memoId}/reactions")
    public MemoResponse toggle(@PathVariable Long teamId, @PathVariable Long memoId,
                               @AuthenticationPrincipal CustomUserDetails p) {
        return memoService.toggleLike(teamId, memoId, p.getUser());
    }
}
