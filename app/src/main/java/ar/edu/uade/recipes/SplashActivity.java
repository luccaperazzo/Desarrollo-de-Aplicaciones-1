package ar.edu.uade.recipes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.splashscreen.SplashScreen;

import ar.edu.uade.recipes.util.UserManager;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIME_OUT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        applyTheme();
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
            boolean onboardingCompleted = prefs.getBoolean("onboarding_completed", false);

            if (onboardingCompleted) {
                UserManager userManager = new UserManager(SplashActivity.this);
                if (userManager.isLoggedIn()) {
                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                } else {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                }
            } else {
                startActivity(new Intent(SplashActivity.this, OnboardingActivity.class));
            }
            finish();
        }, SPLASH_TIME_OUT);
    }

    private void applyTheme() {
        boolean isDarkMode = getSharedPreferences("settings", MODE_PRIVATE).getBoolean("dark_mode", false);
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
