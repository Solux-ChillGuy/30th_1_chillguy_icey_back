package com.project.icey.app.repository;

import com.project.icey.app.domain.SmallTalk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SmallTalkRepository extends JpaRepository<SmallTalk, Long> {
    // 필요시 커스텀 쿼리 추가
}
