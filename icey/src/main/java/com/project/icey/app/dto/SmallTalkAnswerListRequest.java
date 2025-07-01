package com.project.icey.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SmallTalkAnswerListRequest {

    private List<SmallTalkAnswer> answers;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SmallTalkAnswer {
        private Long questionId; // SmallTalk의 id
        private String answer;   // 사용자가 작성한 답변
    }
}
