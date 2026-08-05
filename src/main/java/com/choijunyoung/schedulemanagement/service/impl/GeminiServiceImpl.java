package com.choijunyoung.schedulemanagement.service.impl;

import com.choijunyoung.schedulemanagement.dto.Gemini.FoodAnalysisResponse;
import com.choijunyoung.schedulemanagement.service.GeminiService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class GeminiServiceImpl implements GeminiService {

    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    private final RestClient geminiRestClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String model;

    public GeminiServiceImpl(
            RestClient geminiRestClient,
            ObjectMapper objectMapper,
            @Value("${gemini.api-key}") String apiKey,
            @Value("${gemini.model}") String model
    ) {
        this.geminiRestClient = geminiRestClient;
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.model = model;
    }

    @Override
    public String testConnection(String prompt) {

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of(
                                "parts", List.of(
                                        Map.of("text", prompt)
                                )
                        )
                )
        );

        Map response = callGemini(requestBody);

        return extractText(response);
    }

    @Override
    public String analyzeFood(String foodName) {

        String prompt = """
                음식명: %s

                아래 형식으로 답변해라.

                음식명:
                예상칼로리:
                설명:
                """.formatted(foodName);

        return testConnection(prompt);
    }

    @Override
    public FoodAnalysisResponse analyzeFoodImage(
            MultipartFile file
    ) {
        validateImage(file);

        String mimeType = file.getContentType();
        String base64Image;

        try {
            base64Image = Base64.getEncoder()
                    .encodeToString(file.getBytes());

        } catch (IOException e) {
            throw new IllegalStateException(
                    "이미지 파일을 읽을 수 없습니다.",
                    e
            );
        }

        String prompt = """
                이 음식 사진을 분석해 주세요.

                사진에 보이는 음식을 식별하고,
                일반적인 1인분을 기준으로 예상 칼로리를 계산하세요.

                사진만으로 정확한 양을 알 수 없으므로
                가장 현실적인 하나의 정수 칼로리를 반환하세요.

                description에는 추정 근거와
                사진만으로는 정확하지 않을 수 있다는 내용을
                짧게 작성하세요.
                """;

        Map<String, Object> imagePart = Map.of(
                "inlineData", Map.of(
                        "mimeType", mimeType,
                        "data", base64Image
                )
        );

        Map<String, Object> textPart =
                Map.of("text", prompt);

        Map<String, Object> responseSchema = Map.of(
                "type", "OBJECT",
                "properties", Map.of(
                        "foodName", Map.of(
                                "type", "STRING"
                        ),
                        "estimatedCalories", Map.of(
                                "type", "INTEGER"
                        ),
                        "description", Map.of(
                                "type", "STRING"
                        )
                ),
                "required", List.of(
                        "foodName",
                        "estimatedCalories",
                        "description"
                )
        );

        Map<String, Object> generationConfig = Map.of(
                "responseMimeType", "application/json",
                "responseSchema", responseSchema
        );

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of(
                                "role", "user",
                                "parts", List.of(
                                        imagePart,
                                        textPart
                                )
                        )
                ),
                "generationConfig", generationConfig
        );

        Map response = callGemini(requestBody);

        String jsonText = extractText(response);

        try {
            return objectMapper.readValue(
                    jsonText,
                    FoodAnalysisResponse.class
            );

        } catch (JsonProcessingException e) {
            throw new IllegalStateException(
                    "Gemini 분석 결과를 변환할 수 없습니다. 응답: "
                            + jsonText,
                    e
            );
        }
    }

    private Map callGemini(
            Map<String, Object> requestBody
    ) {
        Map response = geminiRestClient.post()
                .uri(
                        "/v1beta/models/"
                                + model
                                + ":generateContent"
                )
                .header("x-goog-api-key", apiKey)
                .body(requestBody)
                .retrieve()
                .body(Map.class);

        if (response == null) {
            throw new IllegalStateException(
                    "Gemini 응답이 없습니다."
            );
        }

        return response;
    }

    private void validateImage(
            MultipartFile file
    ) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "음식 사진을 선택해 주세요."
            );
        }

        String contentType = file.getContentType();

        if (contentType == null
                || !ALLOWED_IMAGE_TYPES.contains(contentType)) {

            throw new IllegalArgumentException(
                    "JPG, PNG, WEBP 이미지만 분석할 수 있습니다."
            );
        }
    }

    private String extractText(Map response) {

        List candidates =
                (List) response.get("candidates");

        if (candidates == null
                || candidates.isEmpty()) {

            throw new IllegalStateException(
                    "Gemini 응답에 분석 결과가 없습니다."
            );
        }

        Map firstCandidate =
                (Map) candidates.get(0);

        Map content =
                (Map) firstCandidate.get("content");

        if (content == null) {
            throw new IllegalStateException(
                    "Gemini 응답에 content가 없습니다."
            );
        }

        List parts =
                (List) content.get("parts");

        if (parts == null || parts.isEmpty()) {
            throw new IllegalStateException(
                    "Gemini 응답에 parts가 없습니다."
            );
        }

        Map firstPart =
                (Map) parts.get(0);

        Object text =
                firstPart.get("text");

        if (text == null) {
            throw new IllegalStateException(
                    "Gemini 응답에 text가 없습니다."
            );
        }

        return text.toString();
    }
}