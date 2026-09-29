package com.example.api.dto;

/**
 * Request payload for updating the authenticated user's profile.
 */
public class UpdateProfileRequest {

    private String name;
    private String phone;
    private String currentPassword;
    private String newPassword;

    public UpdateProfileRequest() {
    }

    public UpdateProfileRequest(String name, String phone, String currentPassword, String newPassword) {
        this.name = name;
        this.phone = phone;
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
