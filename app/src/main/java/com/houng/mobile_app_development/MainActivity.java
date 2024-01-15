package com.houng.mobile_app_development;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import com.houng.mobile_app_development.modules.screens.SignUpScreen;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start the login activity after 2 seconds
                Intent intent = new Intent(MainActivity.this, SignUpScreen.class);
                startActivity(intent);
                // Close the splash activity so the user can't return to it
                finish();
            }
        }, 2000);
    }
}
