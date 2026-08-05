package com.choijunyoung.schedulemanagement.dto.Gemini;

public record FoodAnalysisResponse(
        String foodName,
        Integer estimatedCalories,
        String description
) {
}