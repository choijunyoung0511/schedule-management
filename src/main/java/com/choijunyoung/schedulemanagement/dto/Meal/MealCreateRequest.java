package com.choijunyoung.schedulemanagement.dto.Meal;

import com.choijunyoung.schedulemanagement.entity.Meal.MealType;
import jakarta.validation.constraints.NotNull;

public class MealCreateRequest {

    @NotNull
    private MealType mealType;

    @NotNull
    private String imageUrl;

    @NotNull
    private Integer amount;

    public MealType getMealType() {
        return mealType;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public Integer getAmount() {
        return amount;
    }
}
