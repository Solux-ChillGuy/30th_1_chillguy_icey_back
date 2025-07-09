package com.project.icey.app.service;

import com.project.icey.app.domain.Card;
import com.project.icey.app.domain.Letter;
import com.project.icey.app.domain.Team;
import com.project.icey.app.dto.CardResponse;
import com.project.icey.app.dto.LetterDetailResponse;
import com.project.icey.app.dto.LetterSummaryResponse;
import com.project.icey.app.dto.WriteInfoResponse;
import com.project.icey.app.repository.CardRepository;
import com.project.icey.app.repository.LetterRepository;
import com.project.icey.app.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LetterService {
    private final CardRepository cardRepo;
    private final TeamRepository teamRepository;
    private final LetterRepository letterRepository;

    //쪽지 작성화면 조회
    @Transactional(readOnly = true)
    public WriteInfoResponse getWriteInfo(Long teamId, Long receiverCardId, Long currentUserId) {

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("해당 팀이 존재하지 않습니다."));

        Card receiver = cardRepo.findById(receiverCardId)
                .orElseThrow(() -> new IllegalArgumentException("받는 명함이 존재하지 않습니다."));

        Card sender = cardRepo.findByTeam_TeamIdAndUserId(teamId, currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("해당 팀에서 보낼 명함이 없습니다."));

        //이렇게 되면 현재 사실상 보내는 사람과 받는 사람이 같은 팀내에 있는가에 대한 유효성 검사가 빠진 상태임.
        if (!receiver.getTeam().getTeamId().equals(teamId)) {
            throw new IllegalArgumentException("받는 명함이 해당 팀에 속해있지 않습니다.");
        }

        CardResponse receiverCardResponse = new CardResponse(
                receiver.getId(),
                receiver.getNickname(),
                receiver.getAnimal(),
                receiver.getProfileColor(),
                receiver.getAccessory(),
                receiver.getMbti(),
                receiver.getHobby(),
                receiver.getSecretTip(),
                receiver.getTmi()
        );

        return new WriteInfoResponse(
                new WriteInfoResponse.CardInfo(sender.getId(), sender.getNickname()),
                receiverCardResponse
        );
    }

    //쪽지 보내기
    @Transactional
    public void sendLetter(Long teamId, Long receiverCardId, Long senderUserId, String content) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("해당 팀이 존재하지 않습니다."));

        Card receiverCard = cardRepo.findById(receiverCardId)
                .filter(c -> c.getTeam().getTeamId().equals(teamId))
                .orElseThrow(() -> new IllegalArgumentException("해당 팀에 존재하지 않는 받는 명함입니다."));

        Card senderCard = cardRepo.findByTeam_TeamIdAndUserId(teamId, senderUserId)
                .orElseThrow(() -> new IllegalArgumentException("해당 팀에서 보낼 명함이 없습니다."));

        Letter letter = Letter.builder()
                .team(team)
                .receiverCard(receiverCard)
                .senderCard(senderCard)
                .content(content)
                .sentAt(LocalDateTime.now())
                .isRead(false)
                .build();

        letterRepository.save(letter);
    }

    //쪽지 상세 조회
    @Transactional
    public LetterDetailResponse readLetter(Long teamId, Long letterId, Long receiverUserId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("해당 팀이 존재하지 않습니다."));

        Card myCard = cardRepo.findByTeam_TeamIdAndUserId(teamId, receiverUserId)
                .orElseThrow(() -> new IllegalArgumentException("해당 팀에 서 내 명함이 존재하지 않습니다."));

        Letter letter = letterRepository.findByLetterIdAndReceiverCard_Id(letterId, myCard.getId())
                .orElseThrow(() -> new IllegalArgumentException("쪽지가 존재하지 않거나 권한이 없습니다."));

        letter.setRead(true);

        return new LetterDetailResponse(
                letter.getSenderCard().getNickname(),
                letter.getContent()
        );


    }

    //받은 쪽지 목록 조회
    @Transactional(readOnly = true)
    public List<LetterSummaryResponse> getReceivedLetters(Long teamId, Long receiverUserId) {

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("해당 팀이 존재하지 않습니다."));

        Card myCard = cardRepo.findByTeam_TeamIdAndUserId(teamId, receiverUserId)
                .orElseThrow(() -> new IllegalArgumentException("해당 팀에서 내 명함이 존재하지 않습니다."));

        return letterRepository.findByTeam_TeamIdAndReceiverCard_IdOrderByCreatedAtDesc(teamId, myCard.getId())
                .stream()
                .map(letter -> new LetterSummaryResponse(
                        letter.getLetterId(),
                        letter.getSenderCard().getNickname(),
                        letter.isRead()
                ))
                .toList();
    }
}
