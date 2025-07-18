package com.project.icey.app.repository;

import com.project.icey.app.domain.MemoReaction;
import com.project.icey.app.domain.MemoReactionId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface MemoReactionRepository extends JpaRepository<MemoReaction, MemoReactionId> {
    Optional<MemoReaction> findByMemo_IdAndUser_Id(Long memoId, Long userId);
    long countByMemo_IdAndLikedTrue(Long memoId);
    List<MemoReaction> findByMemo_IdAndLikedTrue(Long memoId);
    void deleteByMemo_Id(Long memoId);

}
