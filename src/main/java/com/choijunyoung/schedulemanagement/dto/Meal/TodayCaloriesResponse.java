package com.choijunyoung.schedulemanagement.dto.Meal;

public class TodayCaloriesResponse {

    private final int breakfastCalories;
    private final int lunchCalories;
    private final int dinnerCalories;
    private final int snackCalories;
    private final int totalCalories;

    public TodayCaloriesResponse(
            int breakfastCalories,
            int lunchCalories,
            int dinnerCalories,
            int snackCalories
    ) {
        this.breakfastCalories = breakfastCalories;
        this.lunchCalories = lunchCalories;
        this.dinnerCalories = dinnerCalories;
        this.snackCalories = snackCalories;

        this.totalCalories =
                breakfastCalories
                        + lunchCalories
                        + dinnerCalories
                        + snackCalories;
    }

    public int getBreakfastCalories() {
        return breakfastCalories;
    }

    public int getLunchCalories() {
        return lunchCalories;
    }

    public int getDinnerCalories() {
        return dinnerCalories;
    }

    public int getSnackCalories() {
        return snackCalories;
    }

    public int getTotalCalories() {
        return totalCalories;
    }
}