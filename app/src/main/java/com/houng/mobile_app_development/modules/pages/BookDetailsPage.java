package com.houng.mobile_app_development.modules.pages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.houng.mobile_app_development.R;
import com.houng.mobile_app_development.model.Book_model;

import java.util.Objects;

public class BookDetailsPage extends AppCompatActivity {
    public ImageButton buttonEditText;
    public TextView title;
    public ImageView img;
    public ImageView star_outline;
    public ImageView line_star;
    public TextView story;
    public TextView rate;
    public Book_model book;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details_page);
        Toolbar toolbar = findViewById(R.id.materialToolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        int titleTextColor = ContextCompat.getColor(this, R.color.white);
        SpannableString spannableString = new SpannableString(getSupportActionBar().getTitle());
        spannableString.setSpan(new ForegroundColorSpan(titleTextColor), 0, spannableString.length(), 0);
        getSupportActionBar().setTitle(spannableString);

        buttonEditText = findViewById(R.id.buttonEditText);
        title = findViewById(R.id.title);
        img = findViewById(R.id.img);
        story = findViewById(R.id.story);
        rate = findViewById(R.id.rate);

        buttonEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookDetailsPage.this, UpdatePage.class);
                startActivity(intent);
            }
        });

        book = (Book_model) getIntent().getSerializableExtra("EXTRA_DATA");
        if (book != null) {
            title.setText(book.title); if (book.image != null && !book.image.isEmpty()) {
                Glide.with(BookDetailsPage.this)
                    .load(book.image)
                    .into(img);
            }
            story.setText(book.story);
            rate.setText(book.rate + "/5");
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}