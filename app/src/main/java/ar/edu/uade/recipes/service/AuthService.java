package ar.edu.uade.recipes.service;

import ar.edu.uade.recipes.model.LoginRequest;
import ar.edu.uade.recipes.model.LoginResponse;
import ar.edu.uade.recipes.model.RegisterRequest;
import ar.edu.uade.recipes.model.RegisterResponse;
import ar.edu.uade.recipes.model.UpdateUserRequest;
import ar.edu.uade.recipes.model.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface AuthService {
    @POST("/api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("/api/auth/register")
    Call<RegisterResponse> register(@Body RegisterRequest registerRequest);

    @PUT("/api/users/{user_id}")
    Call<User> updateUser(@Path("user_id") String userId, @Body UpdateUserRequest updateUserRequest);
}
