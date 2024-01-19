package com.houng.mobile_app_development.modules.screens;

import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;

import com.houng.mobile_app_development.R;

public class HomeScreen extends Fragment {
    public GridLayout gridLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home_screen, container, false);
        gridLayout = view.findViewById(R.id.gridImage);
        addImagesToGridLayout(gridLayout);
        return view;
    }

    public void addImagesToGridLayout(GridLayout gridLayout) {
        final int totalImages = 10;
        final int columnCount = 3;
        gridLayout.setColumnCount(columnCount);

        // Calculate the image width considering the screen width and spacing
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
        float imageWidthDp = (screenWidthDp - (2 * 16 + 2 * 10)) / columnCount;
        for (int i = 0; i < totalImages; i++) {
            ImageView imageView = new ImageView(getContext());
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = (int) (imageWidthDp * displayMetrics.density);
            params.height = 450;
            params.setMargins(0, 20, 25, 20);
            params.rowSpec = GridLayout.spec(i / columnCount);
            params.columnSpec = GridLayout.spec(i % columnCount);
            params.setGravity(Gravity.CENTER);
            imageView.setLayoutParams(params);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setBackgroundResource(R.drawable.rounded_cornor);
            imageView.setClipToOutline(true);

            @SuppressLint("DiscouragedApi")
            int resId = getResources().getIdentifier("image" + (i + 1), "drawable", getContext().getPackageName());
            if (resId != 0) { // resource was found
                imageView.setImageResource(resId);
            } else {
                imageView.setImageResource(R.drawable.librarybuddy);
            }

            gridLayout.addView(imageView);
        }
    }
}

