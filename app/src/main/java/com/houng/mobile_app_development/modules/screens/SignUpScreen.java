package com.houng.mobile_app_development.modules.screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.houng.mobile_app_development.R;
import com.houng.mobile_app_development.ReadWriteUserDetails;

public class SignUpScreen extends AppCompatActivity {

    public Button btn_register;
    public TextView button_login;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView profileImageView;
    private Button btnSelectImage;
    private Uri imageUri;

    private EditText userNameEditText, emailEditText, passwordEditText, comfirmPasswordEditText;
    private ProgressBar progressBar;
    private static final String TAG = "SignUpScreen";

    @SuppressLint({"ClickableViewAccessibility", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_screen);
        profileImageView = findViewById(R.id.profileImageView);

        Button btnSelectImage = findViewById(R.id.buttonSelectImage);
        btnSelectImage.setOnClickListener(v -> openFileChooser());


        btnSelectImage.setOnClickListener(v -> openFileChooser());
        btn_register = findViewById(R.id.button);
        button_login = findViewById(R.id.txtLogin);
        userNameEditText = findViewById(R.id.userEditText);
        emailEditText = findViewById(R.id.emailEditTextLogin);
        passwordEditText = findViewById(R.id.passwordeditTextLogin);
        comfirmPasswordEditText = findViewById(R.id.comfirmPasswordeditText);
        progressBar = findViewById(R.id.progressBar);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textName = userNameEditText.getText().toString();
                String textEmail = emailEditText.getText().toString();
                String textPassword = passwordEditText.getText().toString();
                String textComfirmPassword = comfirmPasswordEditText.getText().toString();
                if (TextUtils.isEmpty(textName)) {
                    Toast.makeText(SignUpScreen.this, "Please input your name", Toast.LENGTH_LONG).show();
                    userNameEditText.setError("Name Is required");
                    userNameEditText.requestFocus();
                } else if (TextUtils.isEmpty(textEmail)) {
                    Toast.makeText(SignUpScreen.this, "Please input your Email!", Toast.LENGTH_LONG).show();
                    emailEditText.setError("Email is required");
                    emailEditText.requestFocus();
                } else if (TextUtils.isEmpty(textPassword)) {
                    Toast.makeText(SignUpScreen.this, "please input Password here ", Toast.LENGTH_LONG).show();
                    passwordEditText.setError("password is required");
                    emailEditText.requestFocus();
                } else if (TextUtils.isEmpty(textComfirmPassword)) {
                    Toast.makeText(SignUpScreen.this, "please input comfirm Password ", Toast.LENGTH_LONG).show();
                    comfirmPasswordEditText.setError("Comfirm Password is required");
                    comfirmPasswordEditText.requestFocus();
                } else if (TextUtils.equals(textPassword, textComfirmPassword)) {
                    progressBar.setVisibility(View.VISIBLE);
                    registerUser(textName, textEmail, textPassword, textComfirmPassword,"0");

                } else {
                    Toast.makeText(SignUpScreen.this, "comfirm Password is not much with your password", Toast.LENGTH_LONG).show();
                    comfirmPasswordEditText.setError("Comfirm Password must much with your password");
                    comfirmPasswordEditText.requestFocus();

                }

            }
        });

        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpScreen.this, LoginScreen.class);
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

        EditText confirmTextPassword = findViewById(R.id.comfirmPasswordeditText);
        confirmTextPassword.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (confirmTextPassword.getRight() - confirmTextPassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    if (confirmTextPassword.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                        confirmTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        confirmTextPassword.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.eye_outline_icon), null);
                    } else {
                        confirmTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        confirmTextPassword.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.eye_closed_icon), null);
                    }
                    confirmTextPassword.setSelection(confirmTextPassword.getText().length());
                    return true;
                }
            }
            return false;
        });
    }
    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            profileImageView.setImageURI(imageUri);
        }
    }
    private void registerUser(String textuser, String textEmail, String textPassword, String textComfirmPassword, String role) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(textEmail, textPassword).addOnCompleteListener(SignUpScreen.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(textuser).build();
                    firebaseUser.updateProfile(userProfileChangeRequest);

                    uploadImageAndGetUrl(imageUri, imageUrl -> {

                        ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails( textEmail, textPassword, imageUrl, role);

                        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered users");

                        referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    firebaseUser.sendEmailVerification();
                                    Toast.makeText(SignUpScreen.this, "Register Successfully !.please verify your email", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(SignUpScreen.this, LoginScreen.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_NEW_TASK
                                    );
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(SignUpScreen.this, "Register failed !.please try again", Toast.LENGTH_LONG).show();

                                }
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    });




                    //ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textuser, textEmail, textPassword,"");
                } else {
                    try {
                        throw task.getException();

                    } catch (FirebaseAuthWeakPasswordException e) {
                        passwordEditText.setError("your Password is too weak. kindly use a mix of alphabets, number and special characters");
                        passwordEditText.requestFocus();
                        progressBar.setVisibility(View.GONE);
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        emailEditText.setError("please try to use new Email!");
                        emailEditText.requestFocus();
                        progressBar.setVisibility(View.GONE);
                    } catch (FirebaseAuthUserCollisionException e) {
                        userNameEditText.setError("this user is already register");
                        userNameEditText.requestFocus();

                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(SignUpScreen.this, e.getMessage(), Toast.LENGTH_LONG).show();

                    }
                    progressBar.setVisibility(View.GONE);
                }

            }
        });

    }
    private void uploadImageAndGetUrl(Uri imageUri, Callback<String> callback) {
        if (imageUri == null) {
            callback.onResult(null); // or handle error
            return;
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("uploads").child(userId);

        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    callback.onResult(imageUrl);
                }))
                .addOnFailureListener(e -> {
                    Log.e("SignUpScreen", "Upload Image Error: " + e.getMessage());
                    callback.onResult(null); // or handle error
                });
    }

    // ... [Rest of your code]

    interface Callback<T> {
        void onResult(T result);
    }

}