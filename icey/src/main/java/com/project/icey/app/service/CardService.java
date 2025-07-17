package com.project.icey.app.service;

import com.project.icey.app.domain.Card;
import com.project.icey.app.domain.Team;
import com.project.icey.app.domain.User;
import com.project.icey.app.dto.CardRequest;
import com.project.icey.app.dto.CardResponse;
import com.project.icey.app.repository.CardRepository;
import com.project.icey.app.repository.TeamRepository;
import com.project.icey.global.exception.ErrorCode;
import com.project.icey.global.exception.model.CoreApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
public class CardService {

    private final CardRepository cardRepo;
    private final TeamRepository teamRepo;

    /* ---------- 색상 팔레트 (나중에 수정) ---------- */
    private static final List<String> COLORS = List.of(
            "빨강", "파랑", "초록", "노랑", "주황",
            "보라", "분홍", "회색", "민트", "하양"
    );
    private static final Random RANDOM = new Random();
    private String pickRandomColor() {
        return COLORS.get(RANDOM.nextInt(COLORS.size()));
    }

    /* ---------- 조회 ---------- */
    @Transactional(readOnly = true)
    public List<CardResponse> listTemplates(Long userId) {
        return cardRepo.findByUserIdAndTeamIsNull(userId)
                .stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<CardResponse> listTeamCards(Long teamId) {
        return cardRepo.findByTeam_TeamId(teamId)
                .stream().map(this::toDto).toList();
    }

    /* ---------- 템플릿 생성 ---------- */
    public CardResponse createTemplate(User user, CardRequest req) {
        Card tpl = toEntity(req);
        tpl.setUser(user);
        tpl.setProfileColor(null);                // 템플릿엔 색 없음
        return toDto(cardRepo.save(tpl));
    }

    /* ---------- 템플릿 수정 (파생 카드 동기화) ---------- */
    public CardResponse update(Long tplId, Long userId, CardRequest req) {
        Card tpl = cardRepo.findById(tplId)
                .orElseThrow(() -> new CoreApiException(ErrorCode.CARD_TEMPLATE_NOT_FOUND)); // 존재하지 않는 템플릿
        if (!tpl.getUser().getId().equals(userId))
            throw new CoreApiException(ErrorCode.NOT_MY_TEMPLATE); // 내 템플릿이 아님

        apply(tpl, req);
        tpl.regenerateNickname();

        // origin = tplId 파생 카드 전부 동기화
        cardRepo.findByOriginId(tplId).forEach(derived -> {
            apply(derived, req);
            derived.regenerateNickname();
        });
        return toDto(tpl);
    }

    /* ---------- 템플릿 삭제 ---------- */
    public void delete(Long tplId, Long userId) {
        Card tpl = cardRepo.findById(tplId)
                .orElseThrow(() -> new CoreApiException(ErrorCode.CARD_TEMPLATE_NOT_FOUND)); // 존재하지 않는 템플릿
        if (!tpl.getUser().getId().equals(userId))
            throw new CoreApiException(ErrorCode.NOT_MY_TEMPLATE); // 내 템플릿이 아님
        if (cardRepo.existsByOriginId(tplId))
            throw new CoreApiException(ErrorCode.TEMPLATE_IN_USE); // 다른 팀에서 사용 중

        cardRepo.delete(tpl);
    }

    /* ---------- 팀 적용 ---------- */
    public CardResponse applyTemplate(Long teamId, Long tplId, User user) {
        Card tpl = cardRepo.findById(tplId)
                .orElseThrow(() -> new CoreApiException(ErrorCode.CARD_TEMPLATE_NOT_FOUND)); // 템플릿이 없음

        Team team = teamRepo.findById(teamId)
                .orElseThrow(() -> new CoreApiException(ErrorCode.TEAM_NOT_FOUND)); // 팀이 없음

        // 기존 팀 명함 있으면 삭제
        cardRepo.findByTeam_TeamIdAndUserId(teamId, user.getId())
                .ifPresent(cardRepo::delete);

        // 복제 + 색상 랜덤
        Card clone = cloneFromTemplate(tpl, user, team);
        clone.setProfileColor(pickRandomColor());
        return toDto(cardRepo.save(clone));
    }

    /* 템플릿 만든 뒤 바로 적용 => 앞으로 새 명함 만들고 난 뒤 교체 버튼 누를 거면 사용 안 함
    public CardResponse createAndApply(Long teamId, CardRequest req, User user) {
        Card tpl = toEntity(req);
        tpl.setUser(user);
        tpl.setProfileColor(null);
        cardRepo.save(tpl);
        return applyTemplate(teamId, tpl.getId(), user);
    }
    */

    /* ---------- 내부 유틸 ---------- */
    private Card toEntity(CardRequest r) {
        return Card.builder()
                .adjective(r.getAdjective())
                .animal(r.getAnimal())
                .accessory(r.getAccessory())
                .mbti(r.getMbti())
                .hobby(r.getHobby())
                .secretTip(r.getSecretTip())
                .tmi(r.getTmi())
                .profileColor(null)              // 템플릿은 색 없음
                .build();
    }

    private void apply(Card c, CardRequest r) {
        c.setAdjective(r.getAdjective());
        c.setAnimal(r.getAnimal());
        c.setAccessory(r.getAccessory());
        c.setMbti(r.getMbti());
        c.setHobby(r.getHobby());
        c.setSecretTip(r.getSecretTip());
        c.setTmi(r.getTmi());
    }

    private CardResponse toDto(Card c) {
        return new CardResponse(
                c.getId(),
                c.getNickname(),
                c.getAnimal(),
                c.getProfileColor(),            // 색 이름 ("빨강" 등) — 템플릿이면 null
                c.getAccessory(),               // 악세서리 코드 (null 가능)
                c.getHobby(),
                c.getMbti(),
                c.getSecretTip(),
                c.getTmi()
        );
    }

    private Card cloneFromTemplate(Card tpl, User user, Team team) {
        return Card.builder()
                .user(user)
                .team(team)
                .origin(tpl)
                .adjective(tpl.getAdjective())
                .animal(tpl.getAnimal())
                .accessory(tpl.getAccessory())  // NEW
                .mbti(tpl.getMbti())
                .hobby(tpl.getHobby())
                .secretTip(tpl.getSecretTip())
                .tmi(tpl.getTmi())
                .profileColor(null)             // 나중에 pickRandomColor()로 세팅
                .build();
    }
}
