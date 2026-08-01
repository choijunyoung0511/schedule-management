package com.choijunyoung.schedulemanagement.repository;

import com.choijunyoung.schedulemanagement.entity.Meal;
import com.choijunyoung.schedulemanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
public interface MealRepository extends JpaRepository<Meal, Long> {

    List<Meal> findByUserUsernameOrderByCreatedAtDesc(String username);

    String user(User user);

    List<Meal> findByUserUsernameAndCreatedAtBetween(
            String username,
            LocalDateTime start,
            LocalDateTime end
    );
}