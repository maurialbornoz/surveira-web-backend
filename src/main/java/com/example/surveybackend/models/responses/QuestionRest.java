package com.example.surveybackend.models.responses;

import com.example.surveybackend.enums.QuestionType;
import lombok.Data;

import java.util.List;

@Data
public class QuestionRest {
    private Long id;

    private String content;

    private int questionOrder;

    private QuestionType type;

    private List<AnswerRest> answers;
}
