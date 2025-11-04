package ar.edu.uade.recipes.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ar.edu.uade.recipes.model.CartItem;

@Dao
public interface CartDao {
    @Query("SELECT * FROM cart_items ORDER BY created_at DESC")
    List<CartItem> getAllItems();

    @Query("SELECT * FROM cart_items WHERE is_completed = 0 ORDER BY created_at DESC")
    List<CartItem> getActiveItems();

    @Query("SELECT * FROM cart_items WHERE is_completed = 1 ORDER BY created_at DESC")
    List<CartItem> getCompletedItems();

    @Insert
    long insert(CartItem cartItem);

    @Update
    void update(CartItem cartItem);

    @Delete
    void delete(CartItem cartItem);

    @Query("DELETE FROM cart_items WHERE id = :itemId")
    void deleteById(int itemId);

    @Query("DELETE FROM cart_items WHERE is_completed = 1")
    void deleteAllCompleted();

    @Query("DELETE FROM cart_items")
    void deleteAll();
}

