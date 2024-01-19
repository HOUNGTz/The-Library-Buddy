package com.houng.mobile_app_development.modules.pages;

import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.houng.mobile_app_development.R;

public class ProfilePage extends Fragment {
    @Override
    public View onCreateView(
        LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.activity_profile_page, container, false);
            setupClickListener(view, R.id.add_book, AddPage.class);
            setupClickListener(view, R.id.about, AboutUsPage.class);
            return view;
    }

    private void setupClickListener(View view, int layoutId, Class<?> activityClass) {
        LinearLayout layout = view.findViewById(layoutId);
        if (layout != null) {
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Activity activity = getActivity();
                    Log.d("ProfilePage", "Layout clicked. Activity is " + (activity == null ? "null" : "not null"));
                    if (activity != null) {
                        Intent intent = new Intent(activity, activityClass);
                        startActivity(intent);
                    }
                }
            });

        }
    }
}
