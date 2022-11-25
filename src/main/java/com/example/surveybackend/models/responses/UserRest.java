package com.example.surveybackend.models.responses;
import lombok.Data;

@Data
public class UserRest {
    private Long id;

    private String name;

    private String email;
}
