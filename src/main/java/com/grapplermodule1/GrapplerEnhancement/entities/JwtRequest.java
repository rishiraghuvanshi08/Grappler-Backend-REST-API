package com.grapplermodule1.GrapplerEnhancement.entities;

import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
public class JwtRequest {

    private String email;
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
