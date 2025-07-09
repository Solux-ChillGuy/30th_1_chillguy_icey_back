package com.project.icey.app.repository;

import com.project.icey.app.domain.Card;
import com.project.icey.app.domain.Letter;
import com.project.icey.app.domain.Team;
import com.project.icey.app.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;
import java.util.Optional;

public interface LetterRepository extends JpaRepository<Letter, Long> {

    //받은 쪽지 상세 조회 -> letterid랑 받는 사람 카드 id로 조회(어차피 팀당이니까) 지연로딩과 관련한 해결책 반드시 fetch join을 해야 하는 이유
    @Query("SELECT l FROM Letter l JOIN FETCH l.senderCard WHERE l.letterId = :letterId AND l.receiverCard.id = :receiverCardId")
    Optional<Letter> findByLetterIdAndReceiverCard_Id(Long letterId, Long receiverCardId);

}