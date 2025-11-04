package ar.edu.uade.recipes.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import ar.edu.uade.recipes.model.CartItem;
import ar.edu.uade.recipes.model.Recipe;

@Database(entities = {Recipe.class, CartItem.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract RecipeDao recipeDao();
    public abstract CartDao cartDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    "recipes_database"
            )
            .fallbackToDestructiveMigration()
            .build();
        }
        return instance;
    }
}

