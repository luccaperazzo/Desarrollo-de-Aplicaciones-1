package ar.edu.uade.recipes.util;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Helper class para el logging de eventos de Firebase
 */
public class AnalyticsHelper {

    private static FirebaseAnalytics analytics;

    // Eventos
    public static final String EVENT_LOGIN = "login";
    public static final String EVENT_REGISTER = "sign_up";
    public static final String EVENT_VIEW_RECIPE = "view_item";
    public static final String EVENT_ADD_FAVORITE = "add_to_favorites";
    public static final String EVENT_REMOVE_FAVORITE = "remove_from_favorites";
    public static final String EVENT_RATE_RECIPE = "rate_recipe";
    public static final String EVENT_CREATE_RECIPE = "create_recipe";
    public static final String EVENT_EDIT_RECIPE = "edit_recipe";
    public static final String EVENT_DELETE_RECIPE = "delete_recipe";
    public static final String EVENT_SEARCH = "search";
    public static final String EVENT_ADD_TO_CART = "add_to_cart";
    public static final String EVENT_THEME_CHANGE = "theme_change";
    public static final String EVENT_NAVIGATE_TAB = "navigate_tab";

    // Parámetros
    public static final String PARAM_METHOD = "method";
    public static final String PARAM_ITEM_ID = "item_id";
    public static final String PARAM_ITEM_NAME = "item_name";
    public static final String PARAM_ITEM_CATEGORY = "item_category";
    public static final String PARAM_RATING_VALUE = "rating_value";
    public static final String PARAM_SEARCH_TERM = "search_term";
    public static final String PARAM_THEME_MODE = "theme_mode";
    public static final String PARAM_TAB_NAME = "tab_name";
    public static final String PARAM_INGREDIENTS_COUNT = "ingredients_count";
    public static final String PARAM_STEPS_COUNT = "steps_count";
    public static final String PARAM_IS_EDIT_MODE = "is_edit_mode";

    /**
     * Inicializa Firebase Analytics
     */
    public static void initialize(Context context) {
        if (analytics == null) {
            analytics = FirebaseAnalytics.getInstance(context);
        }
    }

    /**
     * Obtiene la instancia de FirebaseAnalytics
     */
    private static FirebaseAnalytics getAnalytics(Context context) {
        if (analytics == null) {
            initialize(context);
        }
        return analytics;
    }

    /**
     * Loguea un evento personalizado
     */
    public static void logEvent(Context context, String eventName, Bundle params) {
        getAnalytics(context).logEvent(eventName, params);
    }

    /**
     * Loguea un evento simple sin parámetros
     */
    public static void logEvent(Context context, String eventName) {
        getAnalytics(context).logEvent(eventName, null);
    }

    // Métodos helper para eventos comunes

    /**
     * Loguea evento de login
     */
    public static void logLogin(Context context, String method) {
        Bundle params = new Bundle();
        params.putString(PARAM_METHOD, method);
        logEvent(context, EVENT_LOGIN, params);
    }

    /**
     * Loguea evento de registro
     */
    public static void logRegister(Context context, String method) {
        Bundle params = new Bundle();
        params.putString(PARAM_METHOD, method);
        logEvent(context, EVENT_REGISTER, params);
    }

    /**
     * Loguea evento de visualización de receta
     */
    public static void logViewRecipe(Context context, String recipeId, String recipeTitle) {
        Bundle params = new Bundle();
        params.putString(PARAM_ITEM_ID, recipeId);
        params.putString(PARAM_ITEM_NAME, recipeTitle);
        params.putString(PARAM_ITEM_CATEGORY, "recipe");
        logEvent(context, EVENT_VIEW_RECIPE, params);
    }

    /**
     * Loguea evento de agregar a favoritos
     */
    public static void logAddFavorite(Context context, String recipeId, String recipeTitle) {
        Bundle params = new Bundle();
        params.putString(PARAM_ITEM_ID, recipeId);
        params.putString(PARAM_ITEM_NAME, recipeTitle);
        logEvent(context, EVENT_ADD_FAVORITE, params);
    }

    /**
     * Loguea evento de remover de favoritos
     */
    public static void logRemoveFavorite(Context context, String recipeId, String recipeTitle) {
        Bundle params = new Bundle();
        params.putString(PARAM_ITEM_ID, recipeId);
        params.putString(PARAM_ITEM_NAME, recipeTitle);
        logEvent(context, EVENT_REMOVE_FAVORITE, params);
    }

    /**
     * Loguea evento de rating de receta
     */
    public static void logRateRecipe(Context context, String recipeId, String recipeTitle, int rating) {
        Bundle params = new Bundle();
        params.putString(PARAM_ITEM_ID, recipeId);
        params.putString(PARAM_ITEM_NAME, recipeTitle);
        params.putInt(PARAM_RATING_VALUE, rating);
        logEvent(context, EVENT_RATE_RECIPE, params);
    }

    /**
     * Loguea evento de creación de receta
     */
    public static void logCreateRecipe(Context context, String recipeId, String recipeTitle,
                                       int ingredientsCount, int stepsCount) {
        Bundle params = new Bundle();
        params.putString(PARAM_ITEM_ID, recipeId);
        params.putString(PARAM_ITEM_NAME, recipeTitle);
        params.putInt(PARAM_INGREDIENTS_COUNT, ingredientsCount);
        params.putInt(PARAM_STEPS_COUNT, stepsCount);
        logEvent(context, EVENT_CREATE_RECIPE, params);
    }

    /**
     * Loguea evento de edición de receta
     */
    public static void logEditRecipe(Context context, String recipeId, String recipeTitle) {
        Bundle params = new Bundle();
        params.putString(PARAM_ITEM_ID, recipeId);
        params.putString(PARAM_ITEM_NAME, recipeTitle);
        logEvent(context, EVENT_EDIT_RECIPE, params);
    }

    /**
     * Loguea evento de eliminación de receta
     */
    public static void logDeleteRecipe(Context context, String recipeId, String recipeTitle) {
        Bundle params = new Bundle();
        params.putString(PARAM_ITEM_ID, recipeId);
        params.putString(PARAM_ITEM_NAME, recipeTitle);
        logEvent(context, EVENT_DELETE_RECIPE, params);
    }

    /**
     * Loguea evento de búsqueda
     */
    public static void logSearch(Context context, String searchTerm) {
        Bundle params = new Bundle();
        params.putString(PARAM_SEARCH_TERM, searchTerm);
        logEvent(context, EVENT_SEARCH, params);
    }

    /**
     * Loguea evento de agregar ingredientes al carrito
     */
    public static void logAddToCart(Context context, String recipeId, String recipeTitle, int ingredientsCount) {
        Bundle params = new Bundle();
        params.putString(PARAM_ITEM_ID, recipeId);
        params.putString(PARAM_ITEM_NAME, recipeTitle);
        params.putInt(PARAM_INGREDIENTS_COUNT, ingredientsCount);
        logEvent(context, EVENT_ADD_TO_CART, params);
    }

    /**
     * Loguea evento de cambio de tema
     */
    public static void logThemeChange(Context context, String themeMode) {
        Bundle params = new Bundle();
        params.putString(PARAM_THEME_MODE, themeMode);
        logEvent(context, EVENT_THEME_CHANGE, params);
    }

    /**
     * Loguea evento de navegación entre tabs
     */
    public static void logNavigateTab(Context context, String tabName) {
        Bundle params = new Bundle();
        params.putString(PARAM_TAB_NAME, tabName);
        logEvent(context, EVENT_NAVIGATE_TAB, params);
    }
}

