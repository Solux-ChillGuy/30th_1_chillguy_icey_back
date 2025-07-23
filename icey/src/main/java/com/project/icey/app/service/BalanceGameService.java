package com.project.icey.app.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.icey.app.domain.BalanceGame;
import com.project.icey.app.domain.BalanceGameVote;
import com.project.icey.app.domain.Team;
import com.project.icey.app.domain.User;
import com.project.icey.app.dto.BalanceGameResultDto;
import com.project.icey.app.repository.BalanceGameRepository;
import com.project.icey.app.repository.BalanceGameVoteRepository;
import com.project.icey.app.repository.TeamRepository;
import com.project.icey.global.exception.ErrorCode;
import com.project.icey.global.exception.model.CoreApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceGameService {

    private final GeminiClientService geminiClientService;
    private final BalanceGameRepository balanceGameRepository;
    private final BalanceGameVoteRepository balanceGameVoteRepository;
    private final TeamRepository teamRepository;

    public BalanceGame createBalanceGame(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new CoreApiException(ErrorCode.RESOURCE_NOT_FOUND));

        long gameCount = balanceGameRepository.countByTeam_TeamId(teamId);
        if (gameCount >= 2) {
            throw new CoreApiException(ErrorCode.GAME_LIMIT_EXCEEDED);
        }
        String prompt = """
        밸런스 게임 질문을 하나만 생성해줘. 아래 조건을 지켜야 해:
        - 질문은 가벼운 분위기의 아이스브레이킹용
        - 질문은 누구나 쉽게 답할 수 있어야 하고, 불편하거나 무거운 주제는 피해야 해
        - 두 선택지는 서로 상반되어 고민되지만 뜬금없는 조합은 아니어야 해
        - 선택지는 되도록 1~2단어로 간결하게
        - 너무 흔한 질문(예: 여름 vs 겨울, 고양이 vs 강아지, 단짠 vs 맵달)은 피해서 창의적으로
        - 형식은 아래처럼 JSON으로 하나만 반환해줘:
            
        {
            "option1": "선택지 1",
            "option2": "선택지 2"
        }
        """;


        String response = null;
        try {
            response = geminiClientService.callGemini(prompt);
            log.info("✅ Gemini 응답: {}", response);

            // 코드블럭 제거
            String cleaned = response.replaceAll("(?s)```json\\s*", "").replaceAll("(?s)```", "").trim();

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(cleaned);

            // 응답이 배열 형태라면 첫 번째 게임만 사용
            JsonNode first = rootNode.isArray() ? rootNode.get(0) : rootNode;

            String option1 = first.path("option1").asText();
            String option2 = first.path("option2").asText();

            BalanceGame game = BalanceGame.builder()
                    .option1(option1)
                    .option2(option2)
                    .team(team)
                    .build();

            return balanceGameRepository.save(game);
        } catch (Exception e) {
            log.error("❌ Gemini LLM 파싱 실패. 응답 내용: {}", response, e);
            throw new CoreApiException(ErrorCode.LLM_SERVER_ERROR, e);
        }
    }

    // 투표
    @Transactional
    public void vote(Long gameId, User user, int selectedOption) {
        BalanceGame game = balanceGameRepository.findById(gameId)
                .orElseThrow(() -> new CoreApiException(ErrorCode.RESOURCE_NOT_FOUND));

        if (selectedOption != 1 && selectedOption != 2) {
            throw new CoreApiException(ErrorCode.INVALID_REQUEST);
        }

        // 투표 여부 확인 (1인 1표, 변경 불가)
        if (balanceGameVoteRepository.existsByBalanceGameIdAndUserId(gameId, user.getId())) {
            throw new CoreApiException(ErrorCode.ALREADY_VOTED);
        }

        BalanceGameVote vote = BalanceGameVote.builder()
                .balanceGame(game)
                .user(user)
                .selectedOption(selectedOption)
                .build();

        balanceGameVoteRepository.save(vote);
    }

    // 투표 결과 조회
    @Transactional(readOnly = true)
    public BalanceGameResultDto getResult(Long gameId) {
        BalanceGame game = balanceGameRepository.findById(gameId)
                .orElseThrow(() -> new CoreApiException(ErrorCode.RESOURCE_NOT_FOUND));

        List<Object[]> counts = balanceGameVoteRepository.countVotesByGameId(gameId);

        long option1Count = 0;
        long option2Count = 0;

        for (Object[] row : counts) {
            int option = (int) row[0];
            long count = (long) row[1];
            if (option == 1) option1Count = count;
            else if (option == 2) option2Count = count;
        }

        long total = option1Count + option2Count;

        return new BalanceGameResultDto(
                gameId,
                game.getOption1(),
                option1Count,
                game.getOption2(),
                option2Count,
                total
        );
    }

    @Transactional
    public void deleteBalanceGame(Long gameId) {
        BalanceGame game = balanceGameRepository.findById(gameId)
                .orElseThrow(() -> new CoreApiException(ErrorCode.RESOURCE_NOT_FOUND));

        // 연관된 투표 삭제 (필요시)
        balanceGameVoteRepository.deleteByBalanceGameId(gameId);
        // 게임 삭제
        balanceGameRepository.delete(game);
    }

    public List<BalanceGameResultDto> getAllGameResultsByTeam(Long teamId) {
        List<BalanceGame> games = balanceGameRepository.findByTeam_TeamId(teamId);

        return games.stream()
                .map(game -> {
                    long option1Count = balanceGameVoteRepository.countByBalanceGameIdAndSelectedOption(game.getId(), 1);
                    long option2Count = balanceGameVoteRepository.countByBalanceGameIdAndSelectedOption(game.getId(), 2);

                    return new BalanceGameResultDto(
                            game.getId(),
                            game.getOption1(),
                            option1Count,
                            game.getOption2(),
                            option2Count,
                            option1Count + option2Count
                    );
                })
                .collect(Collectors.toList());
    }
}
