package com.choijunyoung.schedulemanagement.service;

import com.choijunyoung.schedulemanagement.dto.Gemini.FoodAnalysisResponse;
import org.springframework.web.multipart.MultipartFile;

public interface GeminiService {
    String testConnection(String prompt);
    String analyzeFood(String foodName);

    FoodAnalysisResponse analyzeFoodImage(
            MultipartFile file
    );

}
