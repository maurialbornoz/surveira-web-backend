package com.example.surveybackend.models.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class AnswerCreationRequestModel {
    @NotEmpty
    private String content;
}
