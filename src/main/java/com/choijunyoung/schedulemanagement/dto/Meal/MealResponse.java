package com.choijunyoung.schedulemanagement.dto.Meal;

import com.choijunyoung.schedulemanagement.entity.Meal.Meal;
import com.choijunyoung.schedulemanagement.entity.Meal.MealType;

public class MealResponse {
    private Long id;
    private String imageUrl;
    private MealType mealType;
    private Integer amount;
    private Integer totalCalories;
    private boolean analyzed;

    public MealResponse(Meal meal ) {
        this.id = meal.getId();
        this.mealType = meal.getMealType();
        this.imageUrl = meal.getImageUrl();
        this.amount = meal.getAmount();
        this.totalCalories = meal.getTotalCalories();
        this.analyzed = meal.isAnalyzed();
    }
    public static MealResponse from(Meal meal) {
        return new MealResponse(meal);
    }



    public Long getId() {
        return id;
    }



    public MealType getMealType() {
        return mealType;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public Integer getAmount() {
        return amount;
    }
    public Integer getTotalCalories() {
        return totalCalories;
    }

    public boolean isAnalyzed() {
        return analyzed;
    }
}
