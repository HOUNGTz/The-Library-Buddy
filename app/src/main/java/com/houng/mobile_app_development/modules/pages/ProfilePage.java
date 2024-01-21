package com.houng.mobile_app_development.modules.pages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.houng.mobile_app_development.MainActivity;
import com.houng.mobile_app_development.R;
import com.houng.mobile_app_development.modules.screens.LoginScreen;
import com.houng.mobile_app_development.modules.screens.SignUpScreen;

import java.util.Objects;

public class ProfilePage extends Fragment {
    public LinearLayout evenLogout;

    @Override
    public View onCreateView(
        LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.activity_profile_page, container, false);
            setupClickListener(view, R.id.add_book, AddPage.class);
            setupClickListener(view, R.id.about, AboutUsPage.class);

        evenLogout = view.findViewById(R.id.even_logout);
        if (evenLogout != null) {
            evenLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new StartGameDialogFragment().show(requireActivity().getSupportFragmentManager(), "GAME_DIALOG");
                }
            });
        }
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
                            snackbar.setAction("UNDO", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(getActivity(), "The item has been restored", Toast.LENGTH_SHORT).show(); // Use getActivity() for context
                                }
                            });
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

