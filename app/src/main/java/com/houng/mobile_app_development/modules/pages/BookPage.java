package com.houng.mobile_app_development.modules.pages;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.houng.mobile_app_development.R;

public class BookPage extends Fragment {
    ImageView img_clicker;
    ImageButton information;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_book_page, container, false);

        img_clicker = view.findViewById(R.id.img_clicker);
        if(img_clicker != null){
            img_clicker.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), BookDetailsPage.class);
                startActivity(intent);
            });
        }
        information = view.findViewById(R.id.information);
        information.setOnClickListener(v -> showBottomSheet());

        return view;
    }
    private void showBottomSheet() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireActivity());
        bottomSheetDialog.setContentView(R.layout.dialog_buttom);
        Button add = bottomSheetDialog.findViewById(R.id.button_item_2);
        Button delete = bottomSheetDialog.findViewById(R.id.button_item_3);
        Button cancel = bottomSheetDialog.findViewById(R.id.button_cancel);

        assert add != null;
        add.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Success", Toast.LENGTH_LONG).show();
            bottomSheetDialog.dismiss();
        });

        assert delete != null;
        delete.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Deleted successfully", Toast.LENGTH_LONG).show();
            bottomSheetDialog.dismiss();
        });

        assert cancel != null;
        cancel.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Cancel", Toast.LENGTH_LONG).show();
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }
}