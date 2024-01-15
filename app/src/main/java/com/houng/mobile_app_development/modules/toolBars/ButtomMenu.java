package com.houng.mobile_app_development.modules.toolBars;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.houng.mobile_app_development.R;
import com.houng.mobile_app_development.modules.screens.HomeScreen;

class BottomMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buttom_bar);
        BottomNavigationView navView = findViewById(R.id.navigation);

        // The listener is set correctly here
        navView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.btn_home) {
                Intent intent = new Intent(BottomMenu.this, HomeScreen.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.navigation_button2) {
                return true;
            } else if (itemId == R.id.navigation_button3) {
                return true;
            } else if (itemId == R.id.navigation_button4) {
                return true;
            }
            return false;
        });
    }
};
