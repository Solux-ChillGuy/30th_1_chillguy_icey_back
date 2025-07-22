package com.project.icey.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data @AllArgsConstructor
public class MemoResponse {
    private Long memoId;
    private Long authorId;
    private String authorNickname;
    private String content;
    private boolean liked;
    private long likeCount;
    private List<LikeUser> likeUsers; //닉네임 아니라 animal&profile color map 으로
    private LocalDateTime createdAt;
}
