package com.example.surveybackend.models.request;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class PollCreationRequestModel {
    @NotEmpty
    private String content;

    @NotNull
    private boolean isOpened;

    @Valid
    @NotEmpty
    @Size(min = 1, max = 30)
    private List<QuestionCreationRequestModel> questions;
}
