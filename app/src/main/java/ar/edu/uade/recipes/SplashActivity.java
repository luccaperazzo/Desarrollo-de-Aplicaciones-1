package ar.edu.uade.recipes;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import ar.edu.uade.recipes.util.UserManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Aplicar tema guardado
        applyTheme();

        UserManager userManager = new UserManager(this);

        if (userManager.isLoggedIn()) {
            // Usuario logueado -> ir a la home
            startActivity(new Intent(this, HomeActivity.class));
        } else {
            // Usuario no logueado -> ir al Login
            startActivity(new Intent(this, LoginActivity.class));
        }

        // Cerramos Splash para que no se pueda volver
        finish();
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
