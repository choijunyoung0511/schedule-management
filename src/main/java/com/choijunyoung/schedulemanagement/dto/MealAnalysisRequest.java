package com.choijunyoung.schedulemanagement.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class MealAnalysisRequest {
    @NotNull
    @Min(0)
    private Integer totalCalories;

    public Integer getTotalCalories() {
        return totalCalories;
    }
}
