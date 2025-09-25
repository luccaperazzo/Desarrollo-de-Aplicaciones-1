package ar.edu.uade.recipes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        boolean loggedIn = prefs.getBoolean("logged_in", false);

        if (loggedIn) {
            // Usuario logueado -> ir a la home
            startActivity(new Intent(this, HomeActivity.class));
        } else {
            // Usuario no logueado -> ir al Login
            startActivity(new Intent(this, LoginActivity.class));
        }

        // Cerramos Splash para que no se pueda volver
        finish();
    }
}
