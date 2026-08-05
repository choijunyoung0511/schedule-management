package com.choijunyoung.schedulemanagement.repository;

import com.choijunyoung.schedulemanagement.entity.Meal.Meal;
import com.choijunyoung.schedulemanagement.entity.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
public interface MealRepository extends JpaRepository<Meal, Long> {

    List<Meal> findByUserUsernameOrderByCreatedAtDesc(String username);

    List<Meal> findByUserUsernameAndCreatedAtBetween(
            String username,
            LocalDateTime start,
            LocalDateTime end
    );
}