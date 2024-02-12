package com.houng.mobile_app_development.modules.model;

import java.io.Serializable;

public class Book_model implements Serializable {
    public String title, category, subtitle, image, rate, des, story,id;
    public Book_model() {
        // Default constructor required for calls to DataSnapshot.getValue(Book.class)
    }

    public Book_model(
        String id,
        String title,
        String category,
        String subtitle,
        String downloadUrl,
        String rate,
        String des,
        String story
    ) {
        this.title = title;
        this.category = category;
        this.subtitle = subtitle;
        this.image = downloadUrl;
        this.rate = rate;
        this.des = des;
        this.story = story;
        this.id = id;
    }
}
