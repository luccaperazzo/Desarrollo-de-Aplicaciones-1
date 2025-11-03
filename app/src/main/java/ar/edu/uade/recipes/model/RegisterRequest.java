package ar.edu.uade.recipes.model;

public class RegisterRequest {
    private String email;
    private String username;
    private String full_name;
    private String profile_image_url;
    private String password;
    private boolean is_active;

    public RegisterRequest(String email, String username, String fullName, String profileImageUrl, String password) {
        this.email = email;
        this.username = username;
        this.full_name = fullName;
        this.profile_image_url = profileImageUrl;
        this.password = password;
        this.is_active = true;
    }
}

