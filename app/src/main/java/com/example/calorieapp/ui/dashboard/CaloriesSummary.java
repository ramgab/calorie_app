package com.example.calorieapp.ui.dashboard;

// CaloriesSummary.java
public class CaloriesSummary {
    private String date;
    private int totalCalories;

    public CaloriesSummary(String date, int totalCalories) {
        this.date = date;
        this.totalCalories = totalCalories;
    }

    public String getDate() {
        return date;
    }

    public int getTotalCalories() {
        return totalCalories;
    }
}