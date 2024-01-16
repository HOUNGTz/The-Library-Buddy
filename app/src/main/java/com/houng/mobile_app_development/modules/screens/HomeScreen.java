package com.houng.mobile_app_development.modules.screens;

import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;

import com.houng.mobile_app_development.R;

public class HomeScreen extends Fragment {
    public GridLayout gridLayout;
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home_screen, container, false);
        gridLayout = view.findViewById(R.id.gridLayoutMenu);
        addImagesToGridLayout(gridLayout);

        return view;
    }

    public void addImagesToGridLayout(GridLayout gridLayout) {
        for (int i = 0; i < 9; i++) {
            ImageView newImageView = new ImageView(getContext());
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            newImageView.setLayoutParams(params);// Assuming 'this' is a Context object
            newImageView.setLayoutParams(new GridLayout.LayoutParams());
            newImageView.getLayoutParams().width = 350; // width in pixels
            newImageView.getLayoutParams().height = 500;
            newImageView.setPadding(0, 0, 16, 20);// height in pixels
            newImageView.setScaleType(ImageView.ScaleType.FIT_XY);
            newImageView.setImageResource(R.drawable.librarybuddy);
            gridLayout.addView(newImageView);
        }
    }

}
