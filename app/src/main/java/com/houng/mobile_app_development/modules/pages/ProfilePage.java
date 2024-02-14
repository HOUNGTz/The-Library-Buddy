package com.houng.mobile_app_development.modules.pages;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.houng.mobile_app_development.MainButtomNavigation;
import com.houng.mobile_app_development.R;
import com.houng.mobile_app_development.ReadWriteUserDetails;
import com.houng.mobile_app_development.modules.screens.LoginScreen;

public class ProfilePage extends Fragment {
    public LinearLayout evenLogout;
    public TextView textName, roleText;
    public ImageView imageView;
    public FirebaseAuth auth;
    public String name, image, email, role, password, userID;
    private ProgressBar progressBar;
    public View verticalLine;

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_profile_page, container, false);

        // Initialize your views here
        textName = view.findViewById(R.id.textViewName);
        imageView = view.findViewById(R.id.imageViewProfile);
        roleText = view.findViewById(R.id.role);
        progressBar = view.findViewById(R.id.progressBar);
        verticalLine = view.findViewById(R.id.vertical_line);
        LinearLayout evenLogout = view.findViewById(R.id.even_logout);
        LinearLayout add_book = view.findViewById(R.id.add_book);
        LinearLayout about = view.findViewById(R.id.about);
        LinearLayout update = view.findViewById(R.id.update);

        // Setup Firebase
        auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser == null) {
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
                        userID = firebaseUser.getUid();
                        name = firebaseUser.getDisplayName();
                        image = readWriteUserDetails.imageUrl;
                        role = readWriteUserDetails.role;
                        email = readWriteUserDetails.email;
                        password = readWriteUserDetails.password;

                        // Set the name and role in the TextViews
                        textName.setText(name);
                        if (role.equals("1")) {
                            roleText.setText("Admin");
                            verticalLine.setVisibility(View.VISIBLE);
                            add_book.setVisibility(View.VISIBLE);
                        } else {
                            roleText.setText("User");
                            verticalLine.setVisibility(View.GONE);
                            add_book.setVisibility(View.GONE);
                        }

                        // Load the image using Glide
                        if (readWriteUserDetails.imageUrl != null && !readWriteUserDetails.imageUrl.isEmpty()) {
                            Glide.with(requireActivity())
                                    .load(readWriteUserDetails.imageUrl)
                                    .into(new CustomTarget<Drawable>() {
                                        @Override
                                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                            imageView.setImageDrawable(resource);
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
                            Toast.makeText(getActivity(), "User image not available", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE); // Hide the ProgressBar
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
            evenLogout
                    .setOnClickListener(v -> new StartGameDialogFragment()
                            .show(requireActivity().getSupportFragmentManager(), "GAME_DIALOG"));
        }
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AboutUsPage.class);
                startActivity(intent);
            }
        });
        add_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddPage.class);
                startActivity(intent);
            }
        });

        update.setOnClickListener(v -> new UpdateDialogFragment(
                name,
                email,
                password,
                userID,
                firebaseUser,
                image,
                role
        ).show(requireActivity().getSupportFragmentManager(), "GAME_DIALOG"));

        // Setup click listener for profile image
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (image != null && !image.isEmpty()) {
                    new ProfileImageDialog(image).show(requireActivity().getSupportFragmentManager(), "PROFILE_IMAGE_DIALOG");
                }
            }
        });

        return view;
    }

    // DialogFragment to handle logout confirmation
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
                            intent.setFlags(
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP
                                            | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            | Intent.FLAG_ACTIVITY_NEW_TASK
                            );
                            startActivity(intent);
                        }
                    }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    View rootView = requireActivity().findViewById(android.R.id.content); // Get the root view
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

    // DialogFragment to display the user's profile image
    public static class ProfileImageDialog extends DialogFragment {
        private String imageUrl;

        public ProfileImageDialog(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

            // Create a RelativeLayout to hold the ImageView and Close button
            RelativeLayout layout = new RelativeLayout(requireActivity());
            layout.setLayoutParams(new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));

            // Calculate the desired width and height for the image based on the screen size
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            int dialogWidth = (int) (displayMetrics.widthPixels * 0.9); // 90% of screen width
            int dialogHeight = (int) (dialogWidth * 0.75); // Maintain 4:3 aspect ratio
            RelativeLayout.LayoutParams imageLayoutParams = new RelativeLayout.LayoutParams(
                    dialogWidth,
                    dialogHeight
            );
            imageLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            imageLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP); // Align to the top
            ImageView imageView = new ImageView(requireActivity());
            imageView.setLayoutParams(imageLayoutParams);

            // Load the image using Glide
            Glide.with(requireActivity())
                    .load(imageUrl)
                    .into(imageView);

            // Create the Close button
            Button closeButton = new Button(requireActivity());
            RelativeLayout.LayoutParams closeButtonLayoutParams = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            closeButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT); // Align to the right
            closeButton.setLayoutParams(closeButtonLayoutParams);
            closeButton.setText("Close");
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Close the dialog
                    dismiss();
                }
            });

            // Add the ImageView and Close button to the RelativeLayout
            layout.addView(imageView);
            layout.addView(closeButton);

            builder.setView(layout);

            return builder.create();
        }

    }

    // DialogFragment to handle profile information update
    public static class UpdateDialogFragment extends DialogFragment {
        public FirebaseAuth authProfile;
        private EditText userNameEditText;
        private EditText emailEditText;
        private EditText passwordEditText;
        private ProgressBar progressBar;
        private final String name;
        private final String email;
        private final String password;
        private final String userId;
        private final String imageUri;
        private final String role;
        private final FirebaseUser firebaseUser;

        public UpdateDialogFragment(
                String name,
                String email,
                String password,
                String userId,
                FirebaseUser firebaseUser,
                String imageUri,
                String role
        ) {
            this.name = name;
            this.email = email;
            this.password = password;
            this.userId = userId;
            this.firebaseUser = firebaseUser;
            this.imageUri = imageUri;
            this.role = role;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
            authProfile = FirebaseAuth.getInstance();

            LayoutInflater inflater = requireActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.update_user, null);

            userNameEditText = view.findViewById(R.id.etName);
            emailEditText = view.findViewById(R.id.etEmail);
            passwordEditText = view.findViewById(R.id.etPassword);
            progressBar = view.findViewById(R.id.progressBarUpdate);
            userNameEditText.setText(name);
            emailEditText.setText(email);
            passwordEditText.setText(password);

            builder.setView(view)
                    .setPositiveButton(R.string.start, null) // Set to null temporarily
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Cancel behavior here
                                }
                            }
                    );

            final AlertDialog dialog = builder.create();

            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {

                    Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    positiveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            String textName = userNameEditText.getText().toString();
                            String textEmail = emailEditText.getText().toString();
                            String textPassword = passwordEditText.getText().toString();

                            if (TextUtils.isEmpty(textName)) {
                                userNameEditText.setError("Name Is required");
                                userNameEditText.requestFocus();
                                return; // Stay open
                            } else if (TextUtils.isEmpty(textEmail)) {
                                emailEditText.setError("Email is required");
                                emailEditText.requestFocus();
                                return; // Stay open
                            } else if (TextUtils.isEmpty(textPassword)) {
                                passwordEditText.setError("Password is required");
                                passwordEditText.requestFocus();
                                return; // Stay open
                            }
                            progressBar.setVisibility(View.VISIBLE);
                            ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textEmail, textPassword, imageUri, role, textName);
                            DatabaseReference userReference = FirebaseDatabase
                                    .getInstance()
                                    .getReference("Registered users");

                            userReference.child(userId).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(textName).build();
                                        firebaseUser.updateProfile(profileChangeRequest);
                                        Intent intent = new Intent(getActivity(), MainButtomNavigation.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                                | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                }
                            });
                        }
                    });
                }
            });
            return dialog;
        }
    }
}
