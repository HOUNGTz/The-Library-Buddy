package com.houng.mobile_app_development.modules.screens;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.houng.mobile_app_development.R;
import com.houng.mobile_app_development.ReadWriteUserDetails;
import com.houng.mobile_app_development.model.Book_model;
import com.houng.mobile_app_development.modules.helper.CarouselAdapter;
import com.houng.mobile_app_development.modules.pages.BookDetailsPage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeScreen extends Fragment {
    public GridLayout gridLayout;
    public RecyclerView recyclerView;
    public CarouselAdapter adapter;
    public ImageView profile, image_none;
    private ProgressBar progressBar;
    private final Handler sliderHandler = new Handler();
    public String image;
    public LinearLayout loading;
    private final Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            if (recyclerView != null) {
                int currentPosition = Objects.requireNonNull(recyclerView.getLayoutManager()).getPosition(Objects.requireNonNull(recyclerView.getLayoutManager().getChildAt(0)));
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
        profile = view.findViewById(R.id.profile);
        progressBar = view.findViewById(R.id.progressBar);
        gridLayout = view.findViewById(R.id.gridImage);
        loading = view.findViewById(R.id.loading);
        image_none = view.findViewById(R.id.image_none);
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
        image_none.setVisibility(View.GONE);
        loadUserProfileImage();

        return view;
    }
    public void addImagesToGridLayout(GridLayout gridLayout) {
        final int columnCount = 3;
        gridLayout.setColumnCount(columnCount);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
        float imageWidthDp = (screenWidthDp - (2 * 16 + 2 * 10)) / columnCount;

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Toast.makeText(getActivity(), "User not logged in", Toast.LENGTH_LONG).show();
        } else {
            DatabaseReference referenceBooks = FirebaseDatabase.getInstance().getReference("book");
            referenceBooks.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int imageIndex = 0;
                    boolean foundImage = false;

                    for (DataSnapshot bookSnapshot : dataSnapshot.getChildren()) {
                        if (imageIndex > 6) {
                            break;
                        }
                        Book_model book = bookSnapshot.getValue(Book_model.class);
                        if (book != null) {
                            String image = book.image;
                            if (image != null && !image.isEmpty()) {
                                foundImage = true;
                                image_none.setVisibility(View.GONE);
                                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                                ImageView imageView = new ImageView(getContext());
                                params.width = (int) (imageWidthDp * getResources().getDisplayMetrics().density);
                                params.height = 400;
                                params.setMargins(0, 20, 25, 20);
                                params.rowSpec = GridLayout.spec(imageIndex / columnCount);
                                params.columnSpec = GridLayout.spec(imageIndex % columnCount);
                                imageView.setLayoutParams(params);
                                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                                imageView.setClipToOutline(true);
                                gridLayout.addView(imageView);

                                Glide.with(requireContext())
                                    .load(image)
                                    .skipMemoryCache(true)
                                    .error(R.drawable.empty_image)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .into(imageView);

                                imageIndex++;
                                loading.setVisibility(View.GONE);

                                imageView.setOnClickListener(v -> {
                                    try {
                                        Intent intent = new Intent(getContext(), BookDetailsPage.class);
                                        intent.putExtra("EXTRA_DATA", book); // Directly pass book
                                        startActivity(intent);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                });
                            }
                        }
                    }

                    if (!foundImage) {
                        image_none.setVisibility(View.VISIBLE);
                        Toast.makeText(getActivity(), "No image response", Toast.LENGTH_LONG).show();
                        loading.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getActivity(), "Database error: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
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

    private void loadUserProfileImage() {
        progressBar.setVisibility(View.VISIBLE); // Show the ProgressBar
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser == null) {
            Toast.makeText(getActivity(), "User not logged in", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE); // Hide the ProgressBar
        } else {
            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered users");
            referenceProfile.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ReadWriteUserDetails userDetails = snapshot.getValue(ReadWriteUserDetails.class);
                    if (userDetails != null && userDetails.imageUrl != null && !userDetails.imageUrl.isEmpty()) {
                        Glide.with(requireActivity())
                            .load(userDetails.imageUrl)
                            .into(new CustomTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    profile.setImageDrawable(resource);
                                    progressBar.setVisibility(View.GONE); // Hide the ProgressBar
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {
                                    // Handle cleanup if needed
                                }

                                @Override
                                public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                    progressBar.setVisibility(View.GONE); // Hide the ProgressBar on failure
                                }
                            });
                    } else {
                        Toast.makeText(getActivity(), "Image not available", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressBar.setVisibility(View.GONE); // Hide the ProgressBar on cancellation
                }
            });
        }
    }

}

