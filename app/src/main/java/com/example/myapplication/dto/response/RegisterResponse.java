package com.example.myapplication.dto.response;

public class RegisterResponse {

    private String id;
    private String email;
    private String fullName;

    private String username;
    private String message;

    public RegisterResponse() {
    }

    public RegisterResponse(String id, String email, String fullName, String username, String message) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.username = username;
        this.message = message;
    }

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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
