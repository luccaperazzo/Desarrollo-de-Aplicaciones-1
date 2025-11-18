package ar.edu.uade.recipes.model;

import com.google.gson.annotations.SerializedName;

public class RegisterRequest {
    private String email;
    private String username;
    private String full_name;
    
    @SerializedName("avatar_base64")
    private String avatarBase64;
    
    private String password;
    private boolean is_active;

    public RegisterRequest(String email, String username, String fullName, String avatarBase64, String password) {
        this.email = email;
        this.username = username;
        this.full_name = fullName;
        this.avatarBase64 = avatarBase64;
        this.password = password;
        this.is_active = true;
    }
}

