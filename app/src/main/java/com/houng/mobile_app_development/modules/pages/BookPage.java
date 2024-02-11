package com.houng.mobile_app_development.modules.pages;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.houng.mobile_app_development.R;
import com.houng.mobile_app_development.ReadWriteUserDetails;

public class BookPage extends Fragment {
    public ImageView img_clicker;
    public ImageButton information;
    public FirebaseAuth auth;
    public String role;
    @Override
    public View onCreateView(
        LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    ) {
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

        auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser == null){
            Toast.makeText(getActivity(), "User not logged in", Toast.LENGTH_LONG).show();
        } else {
            DatabaseReference referenceProfile = FirebaseDatabase
                    .getInstance()
                    .getReference("Registered users");
            referenceProfile.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ReadWriteUserDetails readWriteUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                    if (readWriteUserDetails != null) {
                        role = readWriteUserDetails.role;
                        if (role.equals("1")) {
                            assert delete != null;
                            delete.setVisibility(View.VISIBLE);
                            delete.setOnClickListener(v -> {
                                Toast.makeText(getContext(), "Deleted successfully", Toast.LENGTH_LONG).show();
                                bottomSheetDialog.dismiss();
                            });
                        } else {
                            delete.setVisibility(View.GONE);
                        }
                    } else {
                        Toast.makeText(getActivity(), "User details not found", Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }

        assert cancel != null;
        cancel.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Cancel", Toast.LENGTH_LONG).show();
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }
}