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
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
public class CardService {

    private final CardRepository cardRepo;
    private final TeamRepository teamRepo;

    // 색상 팔레트
    private static final List<String> COLORS = List.of(
            "빨강", "파랑", "초록", "노랑", "주황",
            "보라", "분홍", "회색", "민트", "하양"
    );
    private static final Random RANDOM = new Random();

    // [내 명함 템플릿 관련]

    @Transactional(readOnly = true)
    public List<CardResponse> listTemplates(Long userId) {
        return cardRepo.findByUserIdAndTeamIsNull(userId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public CardResponse createTemplate(User user, CardRequest req) {
        Card tpl = toEntity(req);
        tpl.setUser(user);
        tpl.setProfileColor(null); // 템플릿엔 색 없음
        return toDto(cardRepo.save(tpl));
    }

    public CardResponse update(Long tplId, Long userId, CardRequest req) {
        Card tpl = cardRepo.findById(tplId)
                .orElseThrow(() -> new CoreApiException(ErrorCode.CARD_TEMPLATE_NOT_FOUND));
        if (!tpl.getUser().getId().equals(userId))
            throw new CoreApiException(ErrorCode.NOT_MY_TEMPLATE);

        apply(tpl, req);
        tpl.regenerateNickname();

        // origin = tplId 파생 카드 모두 동기화
        cardRepo.findByOriginId(tplId).forEach(derived -> {
            apply(derived, req);
            derived.regenerateNickname();
        });

        return toDto(tpl);
    }

    public void delete(Long tplId, Long userId) {
        Card tpl = cardRepo.findById(tplId)
                .orElseThrow(() -> new CoreApiException(ErrorCode.CARD_TEMPLATE_NOT_FOUND));
        if (!tpl.getUser().getId().equals(userId))
            throw new CoreApiException(ErrorCode.NOT_MY_TEMPLATE);
        if (cardRepo.existsByOriginId(tplId))
            throw new CoreApiException(ErrorCode.TEMPLATE_IN_USE);

        cardRepo.delete(tpl);
    }

    // [팀 명함 관련]

    @Transactional(readOnly = true)
    public List<CardResponse> listTeamCards(Long teamId) {
        return cardRepo.findByTeam_TeamId(teamId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    /*
     팀에 명함을 적용하기
     - 기존 명함이 있으면 내용물과 원본 링크만 교체, 색상은 그대로 유지
     - 기존 명함이 없으면 새로 만들고, 사용 가능한 색상 배정

     applyTemplate 랑 ensureMyCard는 비슷한 기능을 하는데 중복이어도 메서드 확실히 하는 게 좋다고 해서 유지
     */

    public CardResponse applyTemplate(Long teamId, Long tplId, User user) {
        Card tpl = cardRepo.findById(tplId)
                .orElseThrow(() -> new CoreApiException(ErrorCode.CARD_TEMPLATE_NOT_FOUND));
        Team team = teamRepo.findById(teamId)
                .orElseThrow(() -> new CoreApiException(ErrorCode.TEAM_NOT_FOUND));


        Optional<Card> existingCardOpt = cardRepo.findByTeam_TeamIdAndUserId(teamId, user.getId());

        if (existingCardOpt.isPresent()) {
            // 1. 기존 명함이 있으면: 내용물과 원본 링크만 교체, 색상은 그대로 유지
            Card existingCard = existingCardOpt.get();
            apply(existingCard, toRequest(tpl)); // 템플릿의 내용을 기존 카드에 덮어쓰기
            existingCard.setOrigin(tpl);         // 원본(template) 정보만 교체
            existingCard.regenerateNickname();
            return toDto(cardRepo.save(existingCard));

        } else {
            // 2. 기존 명함이 없으면(팀에 처음 입장): 새로 만들고 사용 가능한 색상 배정. 안 쓸 것 같긴 한데 혹시 몰라서
            Card clone = cloneFromTemplate(tpl, user, team);
            clone.setProfileColor(pickAvailableColor(teamId)); //오직 이때만 색상 할당
            return toDto(cardRepo.save(clone));
        }
    }

    /*
     팀에 입장할 때 내 명함을 자동 생성/보장하기
     - 이미 팀카드가 있으면 그대로 반환
     - 없으면 최초 템플릿에서 복사하여 새로 생성하고, 사용 가능한 색상 배정
     */

    public CardResponse ensureMyCard(Long teamId, User user) {
        // 이미 팀카드가 있으면 그대로 반환
        Optional<Card> existing = cardRepo.findByTeam_TeamIdAndUserId(teamId, user.getId());
        if (existing.isPresent()) return toDto(existing.get());

        Team team = teamRepo.findById(teamId)
                .orElseThrow(() -> new CoreApiException(ErrorCode.TEAM_NOT_FOUND));

        Card tpl = getOrCreateFirstTemplate(user);
        Card clone = cloneFromTemplate(tpl, user, team);
        clone.setProfileColor(pickAvailableColor(teamId));
        return toDto(cardRepo.save(clone));
    }

    // 내부 유틸/보조 메서드


    private String pickAvailableColor(Long teamId) {
        List<String> used = cardRepo.findByTeam_TeamId(teamId)
                .stream()
                .map(Card::getProfileColor)
                .toList();
        List<String> available = COLORS.stream()
                .filter(c -> !used.contains(c))
                .toList();
        if (available.isEmpty()) {
            throw new CoreApiException(ErrorCode.NO_AVAILABLE_COLOR);
        }
        return available.get(RANDOM.nextInt(available.size()));
    }

    /*
     - 사용자에게 최초의 명함 템플릿을 보장용!
     - 사용자가 처음 가입했을 때 기본 템플릿 명함 생성
     - 이미 템플릿이 있다면 기존 템플릿을 반환하기!(따로 조건 있으면 추가. 지금은 그냥 첫번째)
     */
    private Card getOrCreateFirstTemplate(User user) {
        List<Card> templates = cardRepo.findByUserIdAndTeamIsNull(user.getId());
        if (!templates.isEmpty()) return templates.get(0);

        // 초기명함1 생성(나중에 수정)
        CardRequest init = new CardRequest();
        init.setAdjective("수줍은");
        init.setAnimal("돼지");
        init.setMbti("INFP");
        init.setHobby("독서");
        init.setSecretTip("사실 수면 시간이 12시간이에요");
        init.setTmi("소싯적 휘파람 챔피언(교정하고 몰락함)");
        init.setAccessory("crown");

        Card tpl = toEntity(init);
        tpl.setUser(user);
        tpl.setProfileColor(null);
        return cardRepo.save(tpl);
    }

    private Card toEntity(CardRequest r) {
        return Card.builder()
                .adjective(r.getAdjective())
                .animal(r.getAnimal())
                .accessory(r.getAccessory())
                .mbti(r.getMbti())
                .hobby(r.getHobby())
                .secretTip(r.getSecretTip())
                .tmi(r.getTmi())
                .profileColor(null)
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

    private CardRequest toRequest(Card card) {
        CardRequest req = new CardRequest();
        req.setAdjective(card.getAdjective());
        req.setAnimal(card.getAnimal());
        req.setAccessory(card.getAccessory());
        req.setMbti(card.getMbti());
        req.setHobby(card.getHobby());
        req.setSecretTip(card.getSecretTip());
        req.setTmi(card.getTmi());
        return req;
    }

    private CardResponse toDto(Card c) {
        Long tplId = (c.getOrigin() != null) ? c.getOrigin().getId() : c.getId();
        return new CardResponse(
                c.getId(),             // cardId
                tplId,                 // templateId
                c.getUser().getId(),   // userId
                c.getNickname(),
                c.getAnimal(),
                c.getProfileColor(),
                c.getAccessory(),
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
                .accessory(tpl.getAccessory())
                .mbti(tpl.getMbti())
                .hobby(tpl.getHobby())
                .secretTip(tpl.getSecretTip())
                .tmi(tpl.getTmi())
                .profileColor(null) // 나중에 setProfileColor
                .build();
    }
}
