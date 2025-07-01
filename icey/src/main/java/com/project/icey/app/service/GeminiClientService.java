package com.project.icey.app.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.icey.app.dto.GeminiRequset;
import com.project.icey.app.dto.GeminiResponse;
import com.project.icey.app.dto.SmallTalkResponse;
import com.project.icey.global.exception.ErrorCode;
import com.project.icey.global.exception.model.CoreApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiService {

    private final RestTemplate restTemplate;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public SmallTalkResponse getSmallTalkQnATips(String target, String purpose) {
        String geminiURL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=" + geminiApiKey;

        String prompt = String.format(
                "스몰톡 대상은 '%s'이고 목적은 '%s'야. " +
                        "다음 형식으로 스몰톡 질문 8개와 각 질문에 대한 팁을 한국어로 JSON 배열로 알려줘. " +
                        "형식: [ { \"question\": \"...\", \"tip\": \"...\" }, ... ]",
                target, purpose
        );

        GeminiRequset request = new GeminiRequset();
        request.createGeminiReqDto(prompt);

        try {
            GeminiResponse response = restTemplate.postForObject(geminiURL, request, GeminiResponse.class);
            String content = response.getCandidates().get(0).getContent().getParts().get(0).getText();

            ObjectMapper objectMapper = new ObjectMapper();
            List<SmallTalkResponse.QuestionTip> tips = objectMapper.readValue(
                    content, new TypeReference<>() {}
            );

            return new SmallTalkResponse(tips);

        } catch (Exception e) {
            log.error("❌ Gemini API 호출 실패", e);
            throw new CoreApiException(ErrorCode.UNKNOWN_ERROR);
        }
    }
}

