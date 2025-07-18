package com.project.icey.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MemoRequest {
    @NotBlank(message = "메모 내용은 비어 있을 수 없습니다!")
    @Size(max = 130, message = "글자수를 넘어섰습니다!")
    //줄 수 10줄 이하: 줄바꿈(\n) 최대 9개 → ^(?:[^\\n]*\\n?){0,10}$
    @Pattern(
            regexp = "^(?:[^\\n]*\\n?){0,10}$",
            message = "최대 10줄까지만 작성 가능합니다!"
    )
    private String content;
}

