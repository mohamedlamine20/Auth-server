package com.personel.auth.server.payload.request;

import com.personel.auth.server.validators.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;

public class LoginRequest {
    @NotBlank
    @Size(min = 3, max = 10, message = "the size must be between 3 and 20")
    private String username;

    @NotBlank
    @ValidPassword(type = HttpStatus.CONFLICT)
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
