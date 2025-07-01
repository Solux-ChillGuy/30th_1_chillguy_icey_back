package com.project.icey.app.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.icey.global.exception.ErrorCode;
import com.project.icey.global.exception.model.CoreApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiClientService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper(); // 응답 가공할 수도 있음

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private static final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=";

    /**
     * LLM 프롬프트를 주고 결과 텍스트를 받아옴 (text 형태)
     */
    public String callGemini(String prompt) {
        String url = GEMINI_URL + geminiApiKey;
        Map<String, Object> requestBody = Map.of(
                "contents", new Object[]{
                        Map.of("parts", new Object[]{Map.of("text", prompt)})
                }
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<?> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);
            log.info("Gemini API 응답 상태 코드: {}", response.getStatusCode());

            Map body = response.getBody();
            log.info("Gemini API 응답 바디: {}", body);

            if (body == null) {
                log.error("Gemini 응답 body가 null 입니다.");
                throw new IllegalStateException("Gemini 응답 body가 null 입니다.");
            }

            if (!body.containsKey("candidates")) {
                log.error("Gemini 응답에 candidates 키가 없습니다. body: {}", body);
                throw new IllegalStateException("Gemini 응답에 candidates 키가 없습니다.");
            }

            List<Map> candidates = (List<Map>) body.get("candidates");
            if (candidates == null || candidates.isEmpty()) {
                log.error("Gemini 응답 candidates 배열이 비어있거나 null 입니다. body: {}", body);
                throw new IllegalStateException("Gemini 응답 candidates 배열이 비어있음");
            }


            Map candidate = candidates.get(0);
            if (!candidate.containsKey("content")) {
                log.error("Gemini 응답 candidate에 content 키가 없습니다. candidate: {}", candidate);
                throw new IllegalStateException("Gemini 응답 candidate에 content 키가 없습니다.");
            }

            Map content = (Map) candidate.get("content");
            List<Map> parts = (List<Map>) content.get("parts");
            if (parts == null || parts.isEmpty()) {
                log.error("Gemini 응답 content.parts 배열이 비어있음. content: {}", content);
                throw new IllegalStateException("Gemini 응답 content.parts 배열이 비어있음");
            }

            Map part = parts.get(0);
            String text = (String) part.get("text");
            if (text == null || text.isEmpty()) {
                log.error("Gemini 응답 text가 null이거나 빈 문자열입니다. part: {}", part);
                throw new IllegalStateException("Gemini 응답 text가 null이거나 빈 문자열입니다.");
            }

            return text;

        } catch (Exception e) {
            log.error("❌ Gemini API 호출 실패", e);
            throw new CoreApiException(ErrorCode.LLM_SERVER_ERROR, e);
        }

    }

}