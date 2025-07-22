package com.project.icey.app.service;

import com.project.icey.app.domain.*;
import com.project.icey.app.dto.MemoRequest;
import com.project.icey.app.dto.MemoResponse;
import com.project.icey.app.repository.*;
import com.project.icey.global.exception.ErrorCode;
import com.project.icey.global.exception.model.CoreApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemoService {

    /* 팀당 메모 한도 (없으면 기본 100) */
    @Value("${memo.board-limit:100}")
    private int boardLimit;

    private final MemoRepository          memoRepo;
    private final MemoReactionRepository  reactRepo;
    private final UserTeamRepository      utmRepo;   // ✅ 이미 있는 Repo 재사용
    private final TeamRepository          teamRepo;
    private final CardRepository cardRepo;

    /* ─────────── 조회 ─────────── */

    @Transactional(readOnly = true)
    public List<MemoResponse> list(Long teamId, Long viewerId) {
        return memoRepo.findByTeamId(teamId)
                .stream().map(m -> toDto(m, viewerId)).toList();
    }

    @Transactional(readOnly = true)
    public MemoResponse detail(Long teamId, Long memoId, Long viewerId) {
        Memo memo = memoRepo.findById(memoId)
                .orElseThrow(() -> new CoreApiException(ErrorCode.MEMO_NOT_FOUND)); // 메모 없음
        validateTeam(memo, teamId);
        return toDto(memo, viewerId);
    }

    /* ─────────── 생성 ─────────── */

    @Transactional
    public MemoResponse create(Long teamId, MemoRequest req, User author) {

        if (memoRepo.countByTeamId(teamId) >= boardLimit)
            throw new CoreApiException(ErrorCode.MEMO_BOARD_LIMIT_EXCEEDED); // 게시판 가득 참

        /* Team 엔티티를 먼저 조회한 뒤 기존 메서드 findByUserAndTeam 사용 */
        Team team = teamRepo.findById(teamId)
                .orElseThrow(() -> new CoreApiException(ErrorCode.TEAM_NOT_FOUND)); // 팀 없음

        UserTeamManager utm = utmRepo.findByUserAndTeam(author, team)
                .orElseThrow(() -> new CoreApiException(ErrorCode.FORBIDDEN_AUTH_EXCEPTION)); // 팀 멤버 아님

        Memo memo = memoRepo.save(
                Memo.builder().utm(utm).content(req.getContent()).build()
        );
        return toDto(memo, author.getId());
    }

    /* ─────────── 수정 ─────────── */

    @Transactional
    public MemoResponse update(Long teamId, Long memoId, MemoRequest req, Long userId) {
        Memo memo = memoRepo.findById(memoId)
                .orElseThrow(() -> new CoreApiException(ErrorCode.MEMO_NOT_FOUND)); // 메모 없음
        validateOwner(memo, teamId, userId);
        memo.updateContent(req.getContent());
        return toDto(memo, userId);
    }

    /* ─────────── 삭제 ─────────── */

    @Transactional
    public void delete(Long teamId, Long memoId, Long userId) {
        Memo memo = memoRepo.findById(memoId)
                .orElseThrow(() -> new CoreApiException(ErrorCode.MEMO_NOT_FOUND)); // 메모 없음
        validateOwner(memo, teamId, userId);
        reactRepo.deleteByMemo_Id(memoId);   // 1) 좋아요 모두 삭제
        memoRepo.delete(memo);              // 2) 이제 메모 삭제
    }

    /* ─────────── 좋아요 토글 ─────────── */

    @Transactional
    public MemoResponse toggleLike(Long teamId, Long memoId, User user) {
        Memo memo = memoRepo.findById(memoId)
                .orElseThrow(() -> new CoreApiException(ErrorCode.MEMO_NOT_FOUND)); // 메모 없음
        validateTeam(memo, teamId);

        Optional<MemoReaction> opt =
                reactRepo.findByMemo_IdAndUser_Id(memoId, user.getId());

        MemoReaction reaction;
        if (opt.isPresent()) {            // ── 이미 눌렀던 경우
            reaction = opt.get();
            reaction.toggle();            //     true <> false
        } else {                          // ── 처음 누르는 경우
            reaction = reactRepo.save(
                    MemoReaction.create(memo, user));     // liked = true
        }

        return toDto(memo, user.getId());
    }

    /* ─────────── 내부 유틸 ─────────── */

    private void validateTeam(Memo memo, Long teamId) {
        /* teamId 필드명이 teamId 이므로 getTeamId()로 비교 */
        if (!memo.getUtm().getTeam().getTeamId().equals(teamId)) {
            throw new CoreApiException(ErrorCode.MEMO_NOT_OF_THIS_TEAM); // 해당 팀 메모 아님
        }
    }

    private void validateOwner(Memo memo, Long teamId, Long userId) {
        validateTeam(memo, teamId);
        if (!memo.getUtm().getUser().getId().equals(userId)) {
            throw new CoreApiException(ErrorCode.NOT_MY_MEMO); // 내가 작성한 메모 아님
        }
    }

    private MemoResponse toDto(Memo m, Long viewerId) {
        boolean liked = reactRepo.findByMemo_IdAndUser_Id(m.getId(), viewerId)
                .map(MemoReaction::isLiked).orElse(false);
        long likeCnt  = reactRepo.countByMemo_IdAndLikedTrue(m.getId());

        //  카드 닉네임&색상 조회
        User author = m.getUtm().getUser();
        Team team   = m.getUtm().getTeam();
        Card authorCard = cardRepo.findByUserAndTeam(author, team)
                .orElseThrow(() -> new CoreApiException(ErrorCode.CARD_NOT_FOUND)); // 카드 없음

        String authorNickname = authorCard.getAdjective() + " " + authorCard.getProfileColor() + " " + authorCard.getAnimal();

        // 좋아요 한 사람 목록(형용사 + 색 + 동물)
        List<String> likeUsers = reactRepo.findByMemo_IdAndLikedTrue(m.getId())
                .stream()
                .map(r -> {
                    Card c = cardRepo.findByUserAndTeam(r.getUser(), team)
                            .orElseThrow(() -> new CoreApiException(ErrorCode.CARD_NOT_FOUND)); // 카드 없음
                    return c.getAdjective() + " " + c.getProfileColor() + " " + c.getAnimal();
                })
                .toList();

        return new MemoResponse(
                m.getId(),
                author.getId(),
                authorNickname,
                m.getContent(),
                liked,
                likeCnt,
                likeUsers,
                m.getCreatedAt()
        );
    }
}
