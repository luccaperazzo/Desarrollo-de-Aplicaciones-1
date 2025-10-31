package ar.edu.uade.recipes.service;

import ar.edu.uade.recipes.model.LoginRequest;
import ar.edu.uade.recipes.model.LoginResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {
    @POST("/api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);
}
