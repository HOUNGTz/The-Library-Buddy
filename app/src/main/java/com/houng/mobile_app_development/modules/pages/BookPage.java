package com.houng.mobile_app_development.modules.pages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.houng.mobile_app_development.R;

public class BookPage extends Fragment {
    ImageView img_clicker;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_book_page, container, false);

        img_clicker = view.findViewById(R.id.img_clicker);
        if(img_clicker != null){
            img_clicker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), BookDetailsPage.class);
                    startActivity(intent);
                }
            });
        }
        return view;
    }
}