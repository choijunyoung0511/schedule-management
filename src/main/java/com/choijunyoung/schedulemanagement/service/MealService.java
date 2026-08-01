package com.choijunyoung.schedulemanagement.service;


import com.choijunyoung.schedulemanagement.dto.Meal.MealCreateRequest;
import com.choijunyoung.schedulemanagement.dto.MealAnalysisRequest;
import com.choijunyoung.schedulemanagement.entity.Meal;
import com.choijunyoung.schedulemanagement.entity.User;
import com.choijunyoung.schedulemanagement.repository.MealRepository;
import com.choijunyoung.schedulemanagement.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;import org.springframework.stereotype.Service;
import com.choijunyoung.schedulemanagement.dto.MealAnalysisRequest;
import com.choijunyoung.schedulemanagement.dto.Meal.TodayCaloriesResponse;
import com.choijunyoung.schedulemanagement.entity.MealType;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.List;

@Service
public class MealService {
    private final MealRepository mealRepository;
    private final UserRepository userRepository;

    public MealService(
                        MealRepository mealRepository,
                       UserRepository userRepository) {
        this.mealRepository = mealRepository;
        this.userRepository = userRepository;
    }
    @Transactional
    public Meal createMeal(
            String username,
            MealCreateRequest request
    ){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException("사용자를 찾을수 없습니다.")
                        );
        Meal meal = new Meal(
                user,
                request.getMealType(),
                request.getImageUrl()
        );
        return mealRepository.save(meal);

    }

    @Transactional(readOnly = true)
    public List<Meal> getMyMeals(String username){
        return mealRepository
                .findByUserUsernameOrderByCreatedAtDesc(username);

    }
    @Transactional
    public Meal completeAnalysis(
            Long mealId,
            String username,
            MealAnalysisRequest request
    ){
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() ->
                        new IllegalArgumentException("식사 기록을 찾을 수 없습니다."));
        if (!meal.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException("본인의 식사 기록만 수정할 수 있습니다.");
        }
        meal.completeAnalysis(request.getTotalCalories());

        return meal;



    }

    @Transactional(readOnly = true)
    public TodayCaloriesResponse getTodayCalories(
            String username
    ) {
        LocalDate today = LocalDate.now();

        LocalDateTime start =
                today.atStartOfDay();

        LocalDateTime end =
                today.plusDays(1).atStartOfDay();

        List<Meal> meals =
                mealRepository
                        .findByUserUsernameAndCreatedAtBetween(
                                username,
                                start,
                                end
                        );

        int breakfastCalories = 0;
        int lunchCalories = 0;
        int dinnerCalories = 0;
        int snackCalories = 0;

        for (Meal meal : meals) {

            if (!meal.isAnalyzed()
                    || meal.getTotalCalories() == null) {
                continue;
            }

            int calories = meal.getTotalCalories();

            if (meal.getMealType()
                    == MealType.BREAKFAST) {

                breakfastCalories += calories;

            } else if (meal.getMealType()
                    == MealType.LUNCH) {

                lunchCalories += calories;

            } else if (meal.getMealType()
                    == MealType.DINNER) {

                dinnerCalories += calories;

            } else if (meal.getMealType()
                    == MealType.SNACK) {

                snackCalories += calories;
            }
        }

        return new TodayCaloriesResponse(
                breakfastCalories,
                lunchCalories,
                dinnerCalories,
                snackCalories
        );
    }
}
