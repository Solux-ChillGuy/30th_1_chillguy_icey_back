package com.project.icey.app.controller;

import com.project.icey.app.domain.SmallTalkList;
import com.project.icey.app.domain.User;
import com.project.icey.app.dto.*;
import com.project.icey.app.service.SmallTalkService;
import com.project.icey.global.dto.ApiResponseTemplete;
import com.project.icey.global.exception.ErrorCode;
import com.project.icey.global.exception.SuccessCode;
import com.project.icey.global.exception.model.CoreApiException;
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
    public ResponseEntity<ApiResponseTemplete<SmallTalkListDto>> previewSmallTalkList(
            @RequestBody SmallTalkCreateRequest request
    ) {
        SmallTalkListDto preview = smallTalkService.previewSmallTalkList(
                null, request.getTarget(), request.getPurpose()
        );
        return ApiResponseTemplete.success(SuccessCode.GET_POST_SUCCESS, preview);
    }

    @PostMapping("/save")
    public ResponseEntity<ApiResponseTemplete<SmallTalkListDto>> saveSmallTalkList(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody SmallTalkListSaveRequest request
    ) {

        if (userDetails == null || userDetails.getUser() == null) {
            // 인증 안 된 사용자 접근 차단 예외 발생
            throw new CoreApiException(ErrorCode.UNAUTHORIZED_EXCEPTION);
        }

        User user = userDetails.getUser();
        SmallTalkList saved = smallTalkService.saveSmallTalkList(request, user);
        SmallTalkListDto result = smallTalkService.getSmallTalkList(saved.getId(), user);
        return ApiResponseTemplete.success(SuccessCode.CREATE_POST_SUCCESS, result);
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponseTemplete<List<SmallTalkListDto>>> getUserSmallTalkLists(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User user = userDetails.getUser();
        List<SmallTalkListDto> listDtos = smallTalkService.getUserSmallTalkLists(user);
        return ApiResponseTemplete.success(SuccessCode.GET_ALL_POST_SUCCESS, listDtos);
    }

    @GetMapping("/list/{listId}")
    public ResponseEntity<ApiResponseTemplete<SmallTalkListDto>> getSmallTalkList(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long listId
    ) {
        User user = userDetails.getUser();
        SmallTalkListDto result = smallTalkService.getSmallTalkList(listId, user);
        return ApiResponseTemplete.success(SuccessCode.GET_POST_SUCCESS, result);
    }

    @DeleteMapping("/talk/{talkId}")
    public ResponseEntity<ApiResponseTemplete<Void>> deleteQuestion(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long talkId
    ) {
        User user = userDetails.getUser();
        smallTalkService.deleteQuestion(talkId, user);
        return ApiResponseTemplete.success(SuccessCode.DELETE_POST_SUCCESS, null);
    }

    @DeleteMapping("/list/{listId}")
    public ResponseEntity<ApiResponseTemplete<Void>> deleteAll(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long listId
    ) {
        User user = userDetails.getUser();
        smallTalkService.deleteAll(listId, user);
        return ApiResponseTemplete.success(SuccessCode.DELETE_POST_SUCCESS, null);
    }

    @PutMapping("/list/{listId}/title")
    public ResponseEntity<ApiResponseTemplete<Void>> updateTitle(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long listId,
            @RequestBody TitleUpdateRequest request
    ) {
        User user = userDetails.getUser();
        smallTalkService.updateListTitle(listId, request.getNewTitle(), user);
        return ApiResponseTemplete.success(SuccessCode.UPDATE_POST_SUCCESS, null);
    }

    @PutMapping("/list/{listId}/edit")
    public ResponseEntity<ApiResponseTemplete<Void>> editSmallTalks(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long listId,
            @RequestBody SmallTalkEditRequest request
    ) {
        User user = userDetails.getUser();
        smallTalkService.editSmallTalks(listId, request.getEdits(), user);
        return ApiResponseTemplete.success(SuccessCode.UPDATE_POST_SUCCESS, null);
    }

    @PostMapping("/talk/{talkId}/swap")
    public ResponseEntity<ApiResponseTemplete<SwapResponse>> swapShow(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long talkId
    ) {
        User user = userDetails.getUser();
        SwapResponse result = smallTalkService.swapShow(talkId, user);
        return ApiResponseTemplete.success(SuccessCode.UPDATE_POST_SUCCESS, result);
    }

}
