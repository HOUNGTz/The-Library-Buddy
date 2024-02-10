package com.houng.mobile_app_development;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.houng.mobile_app_development.modules.pages.BookPage;
import com.houng.mobile_app_development.modules.pages.ProfilePage;
import com.houng.mobile_app_development.modules.pages.SearchPage;
import com.houng.mobile_app_development.modules.screens.HomeScreen;

public class MainButtomNavigation extends AppCompatActivity {
    BottomNavigationView navigationView;
    HomeScreen homeScreen = new HomeScreen();
    BookPage bookPage = new BookPage();
    SearchPage searchPage = new SearchPage();
    ProfilePage profilePage = new ProfilePage();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_buttom_navigation);
        showFragment(homeScreen);
        navigationView = findViewById(R.id.navigation);
        navigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.btn_home) {
                showFragment(homeScreen);
                return true;
            }
//            else if (itemId == R.id.btn_book) {
//                showFragment(bookPage);
//                return true;
//            }
            else if (itemId == R.id.btn_search) {
                showFragment(searchPage);
                return true;
            } else if (itemId == R.id.btn_profile) {
                showFragment(profilePage);
                return true;
            }
            return false;
        });
    }

    private void showFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container,fragment);
        fragmentTransaction.commit();
    }
}