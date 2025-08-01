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

    //팀 내 전체 메모 목록 조회
    @GetMapping
    public List<MemoResponse> list(@PathVariable Long teamId,
                                   @AuthenticationPrincipal CustomUserDetails p) {
        return memoService.list(teamId, p.getUser().getId());
    }

    //팀 내 특정 메모 단건 조회
    @GetMapping("/{memoId}")
    public MemoResponse detail(@PathVariable Long teamId, @PathVariable Long memoId,
                               @AuthenticationPrincipal CustomUserDetails p) {
        return memoService.detail(teamId, memoId, p.getUser().getId());
    }

    //팀에 새 메모 작성
    @PostMapping
    public MemoResponse create(@PathVariable Long teamId,
                               @RequestBody @Valid MemoRequest req,
                               @AuthenticationPrincipal CustomUserDetails p) {
        return memoService.create(teamId, req, p.getUser());
    }

    //메모 수정(내가 쓴 메모만)
    @PutMapping("/{memoId}")
    public MemoResponse update(@PathVariable Long teamId, @PathVariable Long memoId,
                               @RequestBody @Valid MemoRequest req,
                               @AuthenticationPrincipal CustomUserDetails p) {
        return memoService.update(teamId, memoId, req, p.getUser().getId());
    }

    //메모 삭제(내가 쓴 메모만)
    @DeleteMapping("/{memoId}")
    public void delete(@PathVariable Long teamId, @PathVariable Long memoId,
                       @AuthenticationPrincipal CustomUserDetails p) {
        memoService.delete(teamId, memoId, p.getUser().getId());
    }

    //메모 좋아요(토글) - 이미 누르면 취소, 안 눌렀으면 좋아요
    @PostMapping("/{memoId}/reactions")
    public MemoResponse toggle(@PathVariable Long teamId, @PathVariable Long memoId,
                               @AuthenticationPrincipal CustomUserDetails p) {
        return memoService.toggleLike(teamId, memoId, p.getUser());
    }
}
