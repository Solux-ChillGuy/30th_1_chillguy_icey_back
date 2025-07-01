package com.project.icey.app.controller;

import com.project.icey.app.domain.SmallTalkList;
import com.project.icey.app.domain.User;
import com.project.icey.app.dto.*;
import com.project.icey.app.service.SmallTalkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/smalltalk")
public class SmallTalkController {

    private final SmallTalkService smallTalkService;

    @PostMapping("/preview")
    public ResponseEntity<SmallTalkListDto> previewSmallTalkList(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody SmallTalkCreateRequest request
    ) {
        User user = userDetails.getUser();
        SmallTalkListDto preview = smallTalkService.previewSmallTalkList(
                user, request.getTarget(), request.getPurpose()
        );
        return ResponseEntity.ok(preview);
    }


    @PostMapping("/save")
    public ResponseEntity<SmallTalkListDto> saveSmallTalkList(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody SmallTalkListSaveRequest request
    ) {
        User user = userDetails.getUser();
        SmallTalkList saved = smallTalkService.saveSmallTalkList(request, user);
        return ResponseEntity.ok(smallTalkService.getSmallTalkList(saved.getId(), user));
    }


    @GetMapping("/list")
    public ResponseEntity<List<SmallTalkListDto>> getUserSmallTalkLists(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User user = userDetails.getUser();
        List<SmallTalkListDto> listDtos = smallTalkService.getUserSmallTalkLists(user);
        return ResponseEntity.ok(listDtos);
    }


    // 스몰톡 리스트 단건 조회
    @GetMapping("/list/{listId}")
    public ResponseEntity<SmallTalkListDto> getSmallTalkList(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long listId
    ) {
        User user = userDetails.getUser(); // null 체크는 선택적으로
        return ResponseEntity.ok(smallTalkService.getSmallTalkList(listId, user));
    }


    // 질문 하나 삭제
    @DeleteMapping("/talk/{talkId}")
    public ResponseEntity<Void> deleteQuestion(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long talkId
    ) {
        User user = userDetails.getUser();
        smallTalkService.deleteQuestion(talkId, user);
        return ResponseEntity.noContent().build();
    }

    // 리스트 전체 삭제
    @DeleteMapping("/list/{listId}")
    public ResponseEntity<Void> deleteAll(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long listId
    ) {
        User user = userDetails.getUser();
        smallTalkService.deleteAll(listId, user);
        return ResponseEntity.noContent().build();
    }


    // 리스트 제목 수정
    @PutMapping("/list/{listId}/title")
    public ResponseEntity<Void> updateTitle(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long listId,
            @RequestBody TitleUpdateRequest request
    ) {
        User user = userDetails.getUser();
        smallTalkService.updateListTitle(listId, request.getNewTitle(), user);
        return ResponseEntity.ok().build();
    }
}
