package com.example.surveybackend.models.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreatedPollRest {
    private String pollId;
}
