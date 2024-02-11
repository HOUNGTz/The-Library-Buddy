package com.houng.mobile_app_development;

public class ReadWriteUserDetails {
    public String email;
    public String name;
    public String password;
    public String imageUrl;
    public String role = "0";
public  ReadWriteUserDetails(){};
    public  ReadWriteUserDetails(
        String emailText,
        String passwordText,
        String imageUrl,
        String role,
        String name
    ){
        this.email = emailText;
        this.password = passwordText;
        this.imageUrl = imageUrl;
        this.role = role;
        this.name = name;
    }
}
