package com.houng.mobile_app_development.modules.pages;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.houng.mobile_app_development.R;
import com.houng.mobile_app_development.modules.helper.IconAdapterWithTextDialog;

public class BookPage extends Fragment {
    ImageView img_clicker;
    ImageButton information;
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
        information = view.findViewById(R.id.information);
        information.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialog();
            }
        });

        return view;
    }
    public void showBottomSheetDialog() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_buttom, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireActivity());
        bottomSheetDialog.setContentView(view);

        ListView listView = view.findViewById(R.id.listView);
        String[] items = new String[]{"Delete this book", "Add book to favorite"};
        int[] icons = new int[]{R.mipmap.ic_x_mark, R.mipmap.ic_bookmarks};
        IconAdapterWithTextDialog adapter = new IconAdapterWithTextDialog(requireActivity(), items, icons);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((adapterView, view1, position, id) -> {
            bottomSheetDialog.dismiss();
        });
        bottomSheetDialog.show();
    }
}