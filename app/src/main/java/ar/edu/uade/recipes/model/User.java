package ar.edu.uade.recipes.model;

import com.google.gson.annotations.SerializedName;

public class User {
    private String id;
    private String email;
    private String username;
    @SerializedName("full_name")
    private String fullName;
    @SerializedName("profile_image_url")
    private String profileImageUrl;
    @SerializedName("is_active")
    private boolean isActive;

    // Constructor vacío
    public User() {
    }

    // Constructor completo
    public User(String id, String email, String username, String fullName, String profileImageUrl, boolean isActive) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.fullName = fullName;
        this.profileImageUrl = profileImageUrl;
        this.isActive = isActive;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
