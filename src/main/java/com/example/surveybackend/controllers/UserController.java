package com.example.surveybackend.controllers;

import com.example.surveybackend.entities.UserEntity;
import com.example.surveybackend.models.request.UserRegisterRequestModel;
import com.example.surveybackend.models.responses.UserRest;
import com.example.surveybackend.services.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("/hello")
    public String hello(){
        return "Hello";
    }

    @PostMapping
    public UserRest createUser(@RequestBody @Valid UserRegisterRequestModel userModel){
        UserEntity user = userService.createUser(userModel);
        UserRest userRest = new UserRest();
        BeanUtils.copyProperties(user, userRest);
        return userRest;
    }

    @GetMapping
    public UserRest getUser(Authentication authentication){
        String userEmail = authentication.getPrincipal().toString();
        UserEntity user = userService.getUser(userEmail);
        UserRest userRest = new UserRest();
        BeanUtils.copyProperties(user, userRest);
        return userRest;
    }
}
