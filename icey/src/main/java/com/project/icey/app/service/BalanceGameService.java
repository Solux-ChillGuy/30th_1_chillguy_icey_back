package com.project.icey.app.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.icey.app.domain.BalanceGame;
import com.project.icey.app.domain.BalanceGameVote;
import com.project.icey.app.domain.User;
import com.project.icey.app.dto.BalanceGameResultDto;
import com.project.icey.app.repository.BalanceGameRepository;
import com.project.icey.app.repository.BalanceGameVoteRepository;
import com.project.icey.global.exception.ErrorCode;
import com.project.icey.global.exception.model.CoreApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceGameService {

    private final GeminiClientService geminiClientService;
    private final BalanceGameRepository balanceGameRepository;
    private final BalanceGameVoteRepository balanceGameVoteRepository;

    // Gemini로부터 밸런스 게임 생성
    public BalanceGame createBalanceGame(Long teamId) {
        // 팀 아이디 필요 시 prompt에 포함 가능 (생략)

        String prompt = """
            밸런스 게임 주제를 만들어줘. 형식은 다음과 같아.
            선택지는 긴 문장 형태가 아니라 단어 혹은 두 단어 정도로 되도록.
            아래와 같은 형식의 밸런스 게임 json을 1개 생성해줘.
            {
                "title": "게임 제목",
                "option1": "선택지 1 내용",
                "option2": "선택지 2 내용"
            }
            """;

        String response = geminiClientService.callGemini(prompt);

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode node = objectMapper.readTree(response);

            String title = node.path("title").asText();
            String option1 = node.path("option1").asText();
            String option2 = node.path("option2").asText();

            BalanceGame game = BalanceGame.builder()
                    .title(title)
                    .option1(option1)
                    .option2(option2)
                    .build();

            return balanceGameRepository.save(game);
        } catch (Exception e) {
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
                game.getTitle(),
                game.getOption1(),
                option1Count,
                game.getOption2(),
                option2Count,
                total
        );
    }
}
