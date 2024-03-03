package com.baraa.firebase.whatsapp.modelClass;

public class User {

    private String id;
    private String username;
    private String email;
    private String status;
    private String Password;
    private String imgUrl;
    private String state;
    private String isInChat;

    public User() {
    }

    public User(String id, String username, String email, String status, String password, String imgUrl, String state, String isInChat) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.status = status;
        Password = password;
        this.imgUrl = imgUrl;
        this.state = state;
        this.isInChat = isInChat;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getIsInChat() {
        return isInChat;
    }

    public void setIsInChat(String isInChat) {
        this.isInChat = isInChat;
    }
}