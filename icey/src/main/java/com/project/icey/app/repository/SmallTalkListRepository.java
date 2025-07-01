package com.project.icey.app.repository;

import com.project.icey.app.domain.SmallTalkList;
import com.project.icey.app.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SmallTalkListRepository extends JpaRepository<SmallTalkList, Long> {
    List<SmallTalkList> findAllByUser(User user); // User 객체 기준으로 검색
    List<SmallTalkList> findByUserOrderByCreatedAtDesc(User user);
}
