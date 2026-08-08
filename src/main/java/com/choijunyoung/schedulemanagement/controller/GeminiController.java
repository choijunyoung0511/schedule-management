package com.choijunyoung.schedulemanagement.controller;

import com.choijunyoung.schedulemanagement.service.GeminiService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import com.choijunyoung.schedulemanagement.dto.Gemini.FoodAnalysisResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/gemini")
public class GeminiController {

    private final GeminiService geminiService;

    public GeminiController(
            GeminiService geminiService
    ) {
        this.geminiService = geminiService;
    }

    @GetMapping("/test")
    public Map<String, String> test(
            @RequestParam(
                    defaultValue = "안녕하세요"
            ) String prompt
    ) {
        String answer =
                geminiService.testConnection(prompt);

        return Map.of(
                "answer",
                answer
        );
    }
    @GetMapping("/food")
    public Map<String, String> analyzeFood(
            @RequestParam String foodName
    ){
        String answer =
                geminiService.analyzeFood(foodName);
        return Map.of(
                "answer",
                answer
        );
    }
    @PostMapping(
            value = "/analyze-image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public FoodAnalysisResponse analyzeImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("amount") Integer amount
    ) {
        return geminiService.analyzeFoodImage(
                file,
                amount
        );
    }

}