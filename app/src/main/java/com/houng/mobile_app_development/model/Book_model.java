package com.houng.mobile_app_development.model;

public class Book_model {
    public String title, category, subtitle, image, rate, des, story;

    public Book_model() {
        // Default constructor required for calls to DataSnapshot.getValue(Book.class)
    }

    public Book_model(String title, String category, String subtitle, String image, String rate, String des, String story) {
        this.title = title;
        this.category = category;
        this.subtitle = subtitle;
        this.image = image;
        this.rate = rate;
        this.des = des;
        this.story = story;
    }
}
