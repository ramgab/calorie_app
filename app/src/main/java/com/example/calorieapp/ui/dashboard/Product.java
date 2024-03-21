package com.example.calorieapp.ui.dashboard;

public class Product {
    private String name;
    private double calories;
    private double proteins;
    private double fats;
    private double carbohydrates;
    private String composition;
    private String category;

    public Product(String name, double calories, double proteins, double fats, double carbohydrates, String composition, String category) {
        this.name = name;
        this.calories = calories;
        this.proteins = proteins;
        this.fats = fats;
        this.carbohydrates = carbohydrates;
        this.composition = composition;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public double getCalories() {
        return calories;
    }

    public double getProteins() {
        return proteins;
    }

    public double getFats() {
        return fats;
    }

    public double getCarbohydrates() {
        return carbohydrates;
    }

    public String getComposition() {
        return composition;
    }

    public String getCategory() {
        return category;
    }
}
