package com.project.icey.app.domain;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;

@Embeddable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class MemoReactionId implements Serializable {
    @Column(name = "MEMO_ID") private Long memoId;
    @Column(name = "USER_ID")  private Long userId;
}
