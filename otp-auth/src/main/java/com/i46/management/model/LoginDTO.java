package com.i46.management.model;


import lombok.Data;


@Data
public class LoginDTO {
    private String appId;
    private String otp;

    public LoginDTO(){

    }

    public LoginDTO(String appId, String otp) {
        this.appId= appId;
        this.otp = otp;
    }

}
