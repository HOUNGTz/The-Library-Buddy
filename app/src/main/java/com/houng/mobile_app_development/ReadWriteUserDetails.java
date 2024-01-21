package com.houng.mobile_app_development;

public class ReadWriteUserDetails {
    public String email, password, imageUrl, role = "0";

    public  ReadWriteUserDetails( String emailText, String passwordText, String imageUrl,String role){

        this.email = emailText;
        this.password = passwordText;
        this.imageUrl = imageUrl;
        this.role = role;

    }
}
