package com.example.surveybackend.services;

import com.example.surveybackend.entities.UserEntity;
import com.example.surveybackend.models.request.UserRegisterRequestModel;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;


public interface UserService extends UserDetailsService {
    public UserDetails loadUserByUsername(String email);
    public UserEntity createUser(UserRegisterRequestModel user);

    public UserEntity getUser(String email);
}
