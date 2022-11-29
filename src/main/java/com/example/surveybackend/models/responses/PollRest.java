package com.example.surveybackend.models.responses;

import lombok.Data;

import java.util.List;

@Data
public class PollRest {
    private Long id;

    private String pollId;

    private String content;

    private boolean opened;

    private List<QuestionRest> questions;
}
