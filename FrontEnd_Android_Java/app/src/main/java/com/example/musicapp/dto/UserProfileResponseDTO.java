package com.example.musicapp.dto;

public class UserProfileResponseDTO {
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private String membership;

    // getters & setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getMembership() { return membership; }
    public void setMembership(String membership) { this.membership = membership; }
}
