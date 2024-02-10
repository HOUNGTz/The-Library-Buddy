package com.houng.mobile_app_development.modules.screens;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.houng.mobile_app_development.MainButtomNavigation;
import com.houng.mobile_app_development.R;

public class LoginScreen extends AppCompatActivity {
    public TextView btn_login;
    public Button login;
    private EditText email, password;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;
    private static final String TAG = "LoginScreen";

    @SuppressLint({"MissingInflatedId", "ClickableViewAccessibility", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        btn_login = findViewById(R.id.sign_up_pag3);
        login = findViewById(R.id.button_login);
        email = findViewById(R.id.emailEditTextLogin);
        password = findViewById(R.id.passwordeditTextLogin);
        progressBar = findViewById(R.id.progressBar2);
        authProfile = FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textEmail = email.getText().toString();
                String textPassword = password.getText().toString();
                if (TextUtils.isEmpty(textEmail)) {
                    View rootView = findViewById(android.R.id.content); // Get the root view
                    Snackbar snackbar = Snackbar.make(rootView, "Please entry your email.", Snackbar.LENGTH_LONG);
                    snackbar.setDuration(3000);
                    snackbar.setBackgroundTint(getResources().getColor(R.color.black));
                    snackbar.setActionTextColor(getResources().getColor(R.color.white));
                    snackbar.show();
                    email.setError("Email is required");
                    email.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    View rootView = findViewById(android.R.id.content); // Get the root view
                    Snackbar snackbar = Snackbar.make(rootView, "Please re-render email.", Snackbar.LENGTH_LONG);
                    snackbar.setDuration(3000);
                    snackbar.setBackgroundTint(getResources().getColor(R.color.black));
                    snackbar.setActionTextColor(getResources().getColor(R.color.white));
                    snackbar.show();
                    email.setError("Valid email is required");
                } else if (TextUtils.isEmpty(textPassword)) {
                    View rootView = findViewById(android.R.id.content); // Get the root view
                    Snackbar snackbar = Snackbar.make(rootView, "Password is require!", Snackbar.LENGTH_LONG);
                    snackbar.setDuration(3000);
                    snackbar.setBackgroundTint(getResources().getColor(R.color.black));
                    snackbar.setActionTextColor(getResources().getColor(R.color.white));
                    snackbar.show();
                    password.setError("Password is required");
                    password.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    loginUser(textEmail, textPassword);
                }
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginScreen.this, SignUpScreen.class);
                startActivity(intent);
            }
        });

        EditText editTextPassword = findViewById(R.id.passwordeditTextLogin);
        editTextPassword.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (editTextPassword.getRight() - editTextPassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    if (editTextPassword.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                        editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        editTextPassword.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.eye_outline_icon), null);
                    } else {
                        editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        editTextPassword.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.eye_closed_icon), null);
                    }
                    editTextPassword.setSelection(editTextPassword.getText().length());
                    return true;
                }
            }
            return false;
        });

    }

    private void loginUser(String the_email, String psw) {
        authProfile.signInWithEmailAndPassword(the_email, psw).addOnCompleteListener(LoginScreen.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = authProfile.getCurrentUser();
                    if (firebaseUser.isEmailVerified()) {
                        View rootView = findViewById(android.R.id.content); // Get the root view
                        Snackbar snackbar = Snackbar.make(rootView, "User login successfully!", Snackbar.LENGTH_LONG);
                        snackbar.setDuration(3000);
                        snackbar.setBackgroundTint(getResources().getColor(R.color.blue));
                        snackbar.setActionTextColor(getResources().getColor(R.color.white));
                        snackbar.show();
                        if (firebaseUser.isEmailVerified()) {
                            startActivity(new Intent(LoginScreen.this, MainButtomNavigation.class));
                            finish();
                        } else {
                            firebaseUser.sendEmailVerification();
                            authProfile.signOut();
                            showAlertDialog();
                        }
                    } else {
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthInvalidUserException e) {
                            email.setError("User does not Exists or is on longer valid. please Register again.");
                            email.requestFocus();
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            email.setError("Invalid credentials. kindly, check and re-enter");
                            email.requestFocus();
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                            Toast.makeText(LoginScreen.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        View rootView = findViewById(android.R.id.content); // Get the root view
                        Snackbar snackbar = Snackbar.make(rootView, "Something went Wrong!", Snackbar.LENGTH_LONG);
                        snackbar.setDuration(3000);
                        snackbar.setBackgroundTint(getResources().getColor(R.color.black));
                        snackbar.setActionTextColor(getResources().getColor(R.color.white));
                        snackbar.show();

                    }
                }else {
                    View rootView = findViewById(android.R.id.content); // Get the root view
                    Snackbar snackbar = Snackbar.make(rootView, "Something went Wrong!", Snackbar.LENGTH_LONG);
                    snackbar.setDuration(3000);
                    snackbar.setBackgroundTint(getResources().getColor(R.color.black));
                    snackbar.setActionTextColor(getResources().getColor(R.color.white));
                    snackbar.show();
                }

                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginScreen.this);
        builder.setTitle("Email not Verified");
        builder.setMessage("Please verify your email now. you can not login without email verification");
        builder.setPositiveButton(" Continue ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(LoginScreen.this, LoginScreen.class);
                startActivity(intent);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (authProfile.getCurrentUser() != null) {
            View rootView = findViewById(android.R.id.content); // Get the root view
            Snackbar snackbar = Snackbar.make(rootView, "You already logged in.", Snackbar.LENGTH_LONG);
            snackbar.setDuration(3000);
            snackbar.setBackgroundTint(getResources().getColor(R.color.black));
            snackbar.setActionTextColor(getResources().getColor(R.color.white));
            snackbar.show();
            startActivity(new Intent(LoginScreen.this, MainButtomNavigation.class));
            finish();
        } else {
            View rootView = findViewById(android.R.id.content); // Get the root view
            Snackbar snackbar = Snackbar.make(rootView, "You can login now.", Snackbar.LENGTH_LONG);
            snackbar.setDuration(3000);
            snackbar.setBackgroundTint(getResources().getColor(R.color.black));
            snackbar.setActionTextColor(getResources().getColor(R.color.white));
            snackbar.show();
        }
    }
}