package com.example.surveybackend.validators;

import com.example.surveybackend.annotations.UniqueEmail;
import com.example.surveybackend.entities.UserEntity;
import com.example.surveybackend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

    @Autowired
    UserRepository userRepository;
    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        UserEntity user = userRepository.findByEmail(value);
        if(user == null){
            return true;
        }
        return false;
    }
}
