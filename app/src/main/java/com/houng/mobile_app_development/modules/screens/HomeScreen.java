package com.houng.mobile_app_development.modules.screens;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.PagerSnapHelper;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import com.houng.mobile_app_development.R;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.houng.mobile_app_development.modules.helper.CarouselAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeScreen extends Fragment {
    public GridLayout gridLayout;
    public RecyclerView recyclerView;
    public CarouselAdapter adapter;
    private final Handler sliderHandler = new Handler();
    private final Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            if (recyclerView != null) {
                int currentPosition = Objects.requireNonNull(recyclerView.getLayoutManager()).getPosition(recyclerView.getLayoutManager().getChildAt(0));
                int nextPosition = currentPosition + 1;
                if (nextPosition >= adapter.getItemCount()) {
                    nextPosition = 0;
                }
                recyclerView.smoothScrollToPosition(nextPosition);
                sliderHandler.postDelayed(this, 5000);
            }
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home_screen, container, false);
        gridLayout = view.findViewById(R.id.gridImage);
        addImagesToGridLayout(gridLayout);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        List<Integer> imageUrls = new ArrayList<>();
        imageUrls.add(R.drawable.slide1);
        imageUrls.add(R.drawable.slide2);
        imageUrls.add(R.drawable.slide3);
        adapter = new CarouselAdapter(imageUrls);
        recyclerView.setAdapter(adapter);
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
        return view;
    }
    public void addImagesToGridLayout(GridLayout gridLayout) {
        final int totalImages = 10;
        final int columnCount = 3;
        gridLayout.setColumnCount(columnCount);

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
            if (resId != 0) {
                imageView.setImageResource(resId);
            } else {
                imageView.setImageResource(R.drawable.librarybuddy);
            }

            gridLayout.addView(imageView);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, 2000);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        sliderHandler.removeCallbacks(sliderRunnable);
    }
}

