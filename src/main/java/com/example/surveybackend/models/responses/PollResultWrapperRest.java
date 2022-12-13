package com.example.surveybackend.models.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PollResultWrapperRest {
    private List<PollResultRest> results;
    private String content;
    private long id;
}
