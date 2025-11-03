package ar.edu.uade.recipes.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import ar.edu.uade.recipes.model.User;

public class UserManager {
    private static final String PREF_NAME = "recipes_prefs";
    private static final String KEY_USER = "user";
    private static final String KEY_TOKEN = "auth_token";

    private SharedPreferences sharedPreferences;
    private Gson gson;

    public UserManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    // Guardar usuario
    public void saveUser(User user) {
        String userJson = gson.toJson(user);
        sharedPreferences.edit().putString(KEY_USER, userJson).apply();
    }

    // Obtener usuario
    public User getUser() {
        String userJson = sharedPreferences.getString(KEY_USER, null);
        if (userJson != null) {
            return gson.fromJson(userJson, User.class);
        }
        return null;
    }

    // Guardar token
    public void saveToken(String token) {
        sharedPreferences.edit().putString(KEY_TOKEN, token).apply();
    }

    // Obtener token
    public String getToken() {
        return sharedPreferences.getString(KEY_TOKEN, null);
    }

    // Limpiar todo
    public void clearAll() {
        sharedPreferences.edit().clear().apply();
    }

    // Verificar si hay sesión activa
    public boolean isLoggedIn() {
        return getToken() != null && getUser() != null;
    }
}
