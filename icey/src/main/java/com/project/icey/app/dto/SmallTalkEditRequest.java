package com.project.icey.app.dto;

import com.project.icey.app.domain.QuestionType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Data
public class SmallTalkEditRequest {

    private List<EditItem> edits;

    @Data
    public static class EditItem {
        private Long id;              // 기존 질문 id (null이면 새로 추가)
        private String question;      // 수정된 질문 (생성된 질문만 수정 가능)
        private String tip;           // 무시 or null
        private String answer;        // 수정된 답변
        private QuestionType questionType;
        private boolean show;
    }
}
