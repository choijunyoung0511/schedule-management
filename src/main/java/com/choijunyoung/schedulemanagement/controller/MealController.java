package com.choijunyoung.schedulemanagement.controller;


import com.choijunyoung.schedulemanagement.dto.Meal.MealCreateRequest;
import com.choijunyoung.schedulemanagement.entity.Meal.Meal;
import com.choijunyoung.schedulemanagement.service.MealService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.choijunyoung.schedulemanagement.dto.Meal.MealResponse;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import com.choijunyoung.schedulemanagement.dto.Meal.MealAnalysisRequest;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.choijunyoung.schedulemanagement.dto.Meal.TodayCaloriesResponse;

@RestController
@RequestMapping("/meals")
public class MealController {
    private final MealService mealService;

    public MealController(MealService mealService) {
        this.mealService = mealService;
    }
    @PostMapping
    public MealResponse createMeal(
            Authentication authentication,
            @Valid @RequestBody MealCreateRequest request
            ){
        String username = authentication.getName();

        Meal meal = mealService.createMeal(
                username,
                request);

        return new MealResponse(meal);
    }

    @GetMapping
    public List<MealResponse> getMeals(Authentication authentication){
        String username = authentication.getName();

        return mealService.getMyMeals(username)
                .stream()
                .map(MealResponse::from)
                .toList();
    }

    @PatchMapping("/{mealId}/analysis")
    public MealResponse completeAnalysis(
            @PathVariable Long mealId,
            Authentication authentication,
            @Valid @RequestBody MealAnalysisRequest request

    ){
        String username = authentication.getName();

        Meal meal = mealService.completeAnalysis(
                mealId,
                username,
                request
        );
        return new MealResponse(meal);
    }

    @GetMapping("/today")
    public TodayCaloriesResponse getTodayCalories(Authentication authentication){
        String username = authentication.getName();
        return mealService.getTodayCalories(username);
    }

}
