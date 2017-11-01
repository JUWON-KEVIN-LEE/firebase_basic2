package com.immymemine.kevin.firebase_basic2;

/**
 * Created by quf93 on 2017-10-31.
 */

public class User {
    private String id;
    private String token;

    public User(String id, String token) {
        this.id = id; this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getId() {

        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
