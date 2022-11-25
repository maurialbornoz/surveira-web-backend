package com.example.surveybackend.models.request;

import com.example.surveybackend.annotations.UniqueEmail;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class UserRegisterRequestModel {
    @NotEmpty
    private String name;
    @NotEmpty
    @Email
    @UniqueEmail
    private String email;
    @NotEmpty
    @Size(min = 8, max = 40)
    private String password;
}
