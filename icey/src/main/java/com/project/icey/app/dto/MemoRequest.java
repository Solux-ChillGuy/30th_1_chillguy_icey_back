package com.project.icey.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MemoRequest {
    @NotBlank(message = "메모 내용은 비어 있을 수 없습니다!")
    @Size(max = 300, message = "글자수를 넘어섰습니다!")
    private String content;
}
