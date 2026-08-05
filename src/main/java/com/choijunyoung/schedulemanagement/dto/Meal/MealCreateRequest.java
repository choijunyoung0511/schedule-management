package com.choijunyoung.schedulemanagement.dto.Meal;

import com.choijunyoung.schedulemanagement.entity.Meal.MealType;
import jakarta.validation.constraints.NotNull;

public class MealCreateRequest {

    @NotNull
    private MealType mealType;

    @NotNull
    private String imageUrl;

    public MealType getMealType() {
        return mealType;
    }
    public String getImageUrl() {
        return imageUrl;
    }
}
