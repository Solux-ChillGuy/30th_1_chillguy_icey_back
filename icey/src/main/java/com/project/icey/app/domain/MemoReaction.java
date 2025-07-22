package com.project.icey.app.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "MEMO_REACTION")
@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemoReaction {

    @EmbeddedId
    private MemoReactionId id;

    @MapsId("memoId") @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMO_ID")
    private Memo memo;

    @MapsId("userId") @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    @Column(nullable = false)
    private boolean liked = true;

    public static MemoReaction create(Memo m, User u) {
        MemoReaction r = new MemoReaction();
        r.memo = m; r.user = u;
        r.id = new MemoReactionId(m.getId(), u.getId());
        return r;
    }
    public void toggle() { liked = !liked; }
}
