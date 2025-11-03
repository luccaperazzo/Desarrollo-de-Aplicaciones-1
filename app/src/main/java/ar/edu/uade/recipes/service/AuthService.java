package ar.edu.uade.recipes.service;

import ar.edu.uade.recipes.model.LoginRequest;
import ar.edu.uade.recipes.model.LoginResponse;
import ar.edu.uade.recipes.model.RegisterRequest;
import ar.edu.uade.recipes.model.RegisterResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {
    @POST("/api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("/api/auth/register")
    Call<RegisterResponse> register(@Body RegisterRequest registerRequest);
}
