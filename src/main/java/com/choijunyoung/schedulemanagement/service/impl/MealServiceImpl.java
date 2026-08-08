package com.choijunyoung.schedulemanagement.service.impl;

import com.choijunyoung.schedulemanagement.dto.Meal.MealAnalysisRequest;
import com.choijunyoung.schedulemanagement.dto.Meal.MealCreateRequest;
import com.choijunyoung.schedulemanagement.dto.Meal.TodayCaloriesResponse;
import com.choijunyoung.schedulemanagement.entity.Meal.Meal;
import com.choijunyoung.schedulemanagement.entity.Meal.MealType;
import com.choijunyoung.schedulemanagement.entity.User.User;
import com.choijunyoung.schedulemanagement.repository.MealRepository;
import com.choijunyoung.schedulemanagement.repository.UserRepository;
import com.choijunyoung.schedulemanagement.service.MealService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MealServiceImpl implements MealService {

    private final MealRepository mealRepository;
    private final UserRepository userRepository;

    public MealServiceImpl(
            MealRepository mealRepository,
            UserRepository userRepository
    ) {
        this.mealRepository = mealRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Meal createMeal(
            String username,
            MealCreateRequest request
    ) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "사용자를 찾을 수 없습니다."
                        )
                );

        Meal meal = new Meal(
                user,
                request.getMealType(),
                request.getImageUrl(),
                request.getAmount()
        );

        return mealRepository.save(meal);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Meal> getMyMeals(String username) {

        return mealRepository
                .findByUserUsernameOrderByCreatedAtDesc(username);
    }

    @Override
    @Transactional
    public Meal completeAnalysis(
            Long mealId,
            String username,
            MealAnalysisRequest request
    ) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "식사 기록을 찾을 수 없습니다."
                        )
                );

        if (!meal.getUser()
                .getUsername()
                .equals(username)) {

            throw new IllegalArgumentException(
                    "본인의 식사 기록만 수정할 수 있습니다."
            );
        }

        meal.completeAnalysis(
                request.getTotalCalories()
        );

        /*
         * meal은 JPA가 조회한 영속 상태의 엔티티이므로
         * @Transactional이 끝날 때 변경 감지로 UPDATE된다.
         * 따라서 mealRepository.save(meal)을 다시 호출하지 않아도 된다.
         */
        return meal;
    }

    @Override
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

            // 아직 분석되지 않은 기록은 계산에서 제외
            if (!meal.isAnalyzed()
                    || meal.getTotalCalories() == null) {
                continue;
            }

            int calories = meal.getTotalCalories();

            if (meal.getMealType() == MealType.BREAKFAST) {

                breakfastCalories += calories;

            } else if (meal.getMealType() == MealType.LUNCH) {

                lunchCalories += calories;

            } else if (meal.getMealType() == MealType.DINNER) {

                dinnerCalories += calories;

            } else if (meal.getMealType() == MealType.SNACK) {

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
    @Override
    @Transactional
    public void deleteMeal(
            Long mealId,
            String username
    ) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "식사 기록을 찾을 수 없습니다"
                        )
                );
        if (!meal.getUser()
                .getUsername()
                .equals(username)) {
            throw new IllegalArgumentException(
                    "본인 기록만 삭제할 수 있습니다."
            );
        }
        mealRepository.delete(meal);

    }
}