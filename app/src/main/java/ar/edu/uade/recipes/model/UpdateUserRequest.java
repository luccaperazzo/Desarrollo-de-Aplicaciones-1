package ar.edu.uade.recipes.model;

import com.google.gson.annotations.SerializedName;

public class UpdateUserRequest {
    private String email;
    private String username;
    @SerializedName("full_name")
    private String fullName;
    @SerializedName("profile_image_url")
    private String profileImageUrl;
    private String password; // Opcional

    public UpdateUserRequest(String email, String username, String fullName, String profileImageUrl, String password) {
        this.email = email;
        this.username = username;
        this.fullName = fullName;
        this.profileImageUrl = profileImageUrl;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

