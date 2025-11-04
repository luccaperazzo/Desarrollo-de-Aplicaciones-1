package ar.edu.uade.recipes;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import ar.edu.uade.recipes.util.UserManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
}
