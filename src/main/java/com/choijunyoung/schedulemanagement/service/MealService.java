package com.choijunyoung.schedulemanagement.service;

import com.choijunyoung.schedulemanagement.dto.Meal.MealAnalysisRequest;
import com.choijunyoung.schedulemanagement.dto.Meal.MealCreateRequest;
import com.choijunyoung.schedulemanagement.dto.Meal.TodayCaloriesResponse;
import com.choijunyoung.schedulemanagement.entity.Meal.Meal;

import java.util.List;

public interface MealService {

    // 식사 기록 생성
    Meal createMeal(
            String username,
            MealCreateRequest request
    );

    // 로그인한 사용자의 식사 기록 전체 조회
    List<Meal> getMyMeals(String username);

    // 식사 분석 결과 저장
    Meal completeAnalysis(
            Long mealId,
            String username,
            MealAnalysisRequest request
    );




    // 오늘 섭취한 끼니별 칼로리 조회
    TodayCaloriesResponse getTodayCalories(String username);

    void deleteMeal(
            Long mealId,
            String name);
}