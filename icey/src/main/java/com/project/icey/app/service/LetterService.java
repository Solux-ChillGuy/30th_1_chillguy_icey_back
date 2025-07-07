package com.project.icey.app.service;

import com.project.icey.app.domain.Card;
import com.project.icey.app.domain.Team;
import com.project.icey.app.dto.WriteInfoResponse;
import com.project.icey.app.repository.CardRepository;
import com.project.icey.app.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LetterService {
    private final CardRepository cardRepo;
    private final TeamRepository teamRepository;

    //쪽지 작성화면 조회
    @Transactional(readOnly = true)
    public WriteInfoResponse getWriteInfo(Long teamId, Long receiverCardId, Long currentUserId) {

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("해당 팀이 존재하지 않습니다."));
        Card receiver = cardRepo.findById(receiverCardId)
                .orElseThrow(() -> new IllegalArgumentException("받는 명함이 존재하지 않습니다."));

        Card sender = cardRepo.findByTeam_TeamIdAndUserId(teamId, currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("해당 팀에서 보낼 명함이 없습니다."));

        return new WriteInfoResponse(
                new WriteInfoResponse.CardInfo(sender.getId(), sender.getNickname()),
                new WriteInfoResponse.CardInfo(receiver.getId(), receiver.getNickname())
        );
    }
}
