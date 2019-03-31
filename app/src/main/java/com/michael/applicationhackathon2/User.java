package com.michael.applicationhackathon2;

public class User {
    public String Uid;
    public String username;
    public String email;
    public String password;
    public String enrollno;
    public String mobphone;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String Uid,String username, String email,String password,String enrollno,String mobphone) {
        this.Uid = Uid;
        this.username = username;
        this.email = email;
        this.enrollno = enrollno;
        this.mobphone = mobphone;
        this.password = password;
    }
}
