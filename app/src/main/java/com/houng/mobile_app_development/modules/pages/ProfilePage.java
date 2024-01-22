package com.houng.mobile_app_development.modules.pages;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.houng.mobile_app_development.R;
import com.houng.mobile_app_development.ReadWriteUserDetails;
import com.houng.mobile_app_development.modules.screens.LoginScreen;

public class ProfilePage extends Fragment {
    public LinearLayout evenLogout;
    public LinearLayout about;
    public TextView textName, roleText;
    public ImageView imageView;
    public FirebaseAuth auth;
    public String name, image,role;

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_profile_page, container, false);

        // Initialize your views here
        textName = view.findViewById(R.id.textViewName);
        imageView = view.findViewById(R.id.imageViewProfile);
        LinearLayout evenLogout = view.findViewById(R.id.even_logout);
        LinearLayout add_book = view.findViewById(R.id.add_book);
        roleText = view.findViewById(R.id.role);

        // Setup Firebase
        auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser == null){
            Toast.makeText(getActivity(), "User not logged in", Toast.LENGTH_LONG).show();
        } else {
            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered users");
            referenceProfile.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ReadWriteUserDetails readWriteUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                    if (readWriteUserDetails != null) {
                        name = firebaseUser.getDisplayName();
                        image = readWriteUserDetails.imageUrl;
                        role = readWriteUserDetails.role;

                        // Set the name and role in the TextViews
                        textName.setText(name);
                        if (role.equals("1")) {
                            roleText.setText("Admin");
                            add_book.setVisibility(View.VISIBLE);
                        } else {
                            roleText.setText("User");
                            add_book.setVisibility(View.GONE);
                        }

                        // Load the image using Glide
                        if (image != null && !image.isEmpty()) {
                            Glide.with(ProfilePage.this)
                                    .load(image)
                                    .into(imageView);
                        }
                    } else {
                        Toast.makeText(getActivity(), "User details not found", Toast.LENGTH_LONG).show();
                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            if (image != null && !image.isEmpty()) {
                Glide.with(ProfilePage.this)
                        .load(image)
                        .into(imageView);
            }
        }

        // Setup logout event
        if (evenLogout != null) {
            evenLogout.setOnClickListener(v -> new StartGameDialogFragment().show(requireActivity().getSupportFragmentManager(), "GAME_DIALOG"));
        }


        about = view.findViewById(R.id.about);
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(getActivity(), AboutUsPage.class);
                startActivity(intent);
            }
        });
        add_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(getActivity(), AddPage.class);
                startActivity(intent);
            }
        });
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
    public static class StartGameDialogFragment extends DialogFragment {
        public FirebaseAuth authProfile;

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
            authProfile = FirebaseAuth.getInstance();
            builder.setIcon(R.mipmap.warning_bulelight_icon)
                .setMessage(R.string.dialog_start_game)
                .setPositiveButton(R.string.start, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        authProfile.signOut();
                        Intent intent = new Intent(getActivity(), LoginScreen.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_NEW_TASK
                        );
                        startActivity(intent);
                    }
                })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            View rootView = getActivity().findViewById(android.R.id.content); // Get the root view
                            Snackbar snackbar = Snackbar.make(rootView, "The account wasn't logged out.", Snackbar.LENGTH_LONG);
                            snackbar.setDuration(3000);
                            snackbar.setBackgroundTint(getResources().getColor(R.color.colorPrimaryDark));
                            snackbar.setActionTextColor(getResources().getColor(R.color.white));
                            snackbar.show();
                        }
                    }
            );
            return builder.create();
        }
    }
}

