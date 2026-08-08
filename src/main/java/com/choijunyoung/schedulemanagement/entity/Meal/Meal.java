package com.choijunyoung.schedulemanagement.entity.Meal;

import com.choijunyoung.schedulemanagement.entity.User.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "meals")
public class Meal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 여러 식사 기록이 한 명의 사용자에게 속함
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 아침, 점심, 저녁, 간식 구분
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MealType type;

    // 음식 이미지 주소
    @Column(nullable = false, length = 500)
    private String imageUrl;


    @Column(nullable = false)
    private Integer amount;

    // 분석 완료 후 총칼로리
    private Integer totalCalories;

    // 분석 완료 여부
    @Column(nullable = false)
    private boolean analyzed;

    // 식사 기록 생성 시간
    @Column(nullable = false)
    private LocalDateTime createdAt;

    // JPA가 사용하는 기본 생성자
    protected Meal() {
    }

    public Meal(
            User user,
            MealType mealType,
            String imageUrl,
            Integer amount
    ) {
        this.user = user;
        this.type = mealType;
        this.imageUrl = imageUrl;
        this.amount = amount;
        this.totalCalories = null;
        this.analyzed = false;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public MealType getMealType() {
        return type;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Integer getTotalCalories() {
        return totalCalories;
    }

    public boolean isAnalyzed() {
        return analyzed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void completeAnalysis(Integer totalCalories) {
        this.totalCalories = totalCalories;
        this.analyzed = true;
    }
    public Integer getAmount() {
        return amount;
    }


}