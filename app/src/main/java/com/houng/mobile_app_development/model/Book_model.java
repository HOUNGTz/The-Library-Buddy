package com.houng.mobile_app_development.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Book_model implements Parcelable {
    public static final Creator<Book_model> CREATOR = new Creator<Book_model>() {
        @Override
        public Book_model createFromParcel(Parcel in) {
            return new Book_model(in);
        }

        @Override
        public Book_model[] newArray(int size) {
            return new Book_model[size];
        }
    };

    public Book_model() {
        // Default constructor required for calls to DataSnapshot.getValue(Book.class)
    }

    public Book_model(String id, String title, String category, String subtitle, String downloadUrl, String rate, String des, String story) {
        this.title = title;
        this.category = category;
        this.subtitle = subtitle;
        this.image = downloadUrl;
        this.rate = rate;
        this.des = des;
        this.story = story;
        this.id = id;
    }
    public String title, category, subtitle, image, rate, des, story, id;

    protected Book_model(Parcel in) {
        title = in.readString();
        category = in.readString();
        subtitle = in.readString();
        image = in.readString();
        rate = in.readString();
        des = in.readString();
        story = in.readString();
        id = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(category);
        dest.writeString(subtitle);
        dest.writeString(image);
        dest.writeString(rate);
        dest.writeString(des);
        dest.writeString(story);
        dest.writeString(id);
    }
}
