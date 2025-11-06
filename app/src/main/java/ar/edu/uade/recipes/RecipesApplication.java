package ar.edu.uade.recipes;

import android.app.Application;

import com.google.firebase.FirebaseApp;

/**
 * Clase Application para inicializar Firebase
 */
public class RecipesApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Inicializar Firebase
        FirebaseApp.initializeApp(this);
    }
}

