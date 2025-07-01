package com.project.icey.app.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.icey.app.dto.SmallTalkResponse;
import com.project.icey.global.exception.ErrorCode;
import com.project.icey.global.exception.model.CoreApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SmallTalkGeneratorService {

    private final GeminiClientService geminiClientService;

    public SmallTalkResponse generateSmallTalkWithTitle(String target, String purpose) {
        String prompt = String.format(
                "스몰톡 대상은 '%s'이고 목적은 '%s'야. " +
                        "다음 형식으로 제목과 스몰톡 질문 8개와 각 질문에 대한 팁을 JSON 배열로 알려줘. 대상에 적합한 말투를 사용하여 대상과 어색함을 풀 수 있는 아이스 브레이킹이 가능하도록 해줘. 이모지는 사용하지 말아줘 \n" +
                        "제목은 간단한 단어 형태로 ## 으로 시작하고, 질문과 팁은 아래처럼 JSON 배열로 알려줘:\n" +
                        "## 제목 텍스트\n" +
                        "```json\n" +
                        "[{ \"question\": \"...\", \"tip\": \"...\" }, ...]\n" +
                        "```",
                target, purpose
        );

        String response = geminiClientService.callGemini(prompt);

        // 응답 예시:
        // ## 제목 텍스트
        // ```json
        // [ ... 질문 배열 ... ]
        // ```

        String title = null;
        String jsonPart = null;

        // 제목 추출: 첫 줄에서 ## 로 시작하는 제목 찾기
        String[] lines = response.split("\n");
        for (String line : lines) {
            if (line.startsWith("##")) {
                title = line.substring(2).trim();
                break;
            }
        }

        // JSON 코드블록 부분 추출
        int start = response.indexOf("```json");
        int end = response.lastIndexOf("```");
        if (start >= 0 && end > start) {
            jsonPart = response.substring(start + 7, end).trim(); // 7은 "```json".length()
        } else {
            // 코드블록 못찾으면 전체를 jsonPart로 시도하거나 예외 처리
            jsonPart = response.trim();
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<SmallTalkResponse.QuestionTip> questionTips = objectMapper.readValue(
                    jsonPart, new TypeReference<List<SmallTalkResponse.QuestionTip>>() {}
            );

            return new SmallTalkResponse(title, questionTips);
        } catch (Exception e) {
            throw new CoreApiException(ErrorCode.LLM_SERVER_ERROR, e);
        }
    }
}
