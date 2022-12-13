package com.example.surveybackend.interfaces;

public interface PollResult {
    int getQuestionOrder();
    long getQuestionId();
    String getQuestion();
    long getAnswerId();
    String getAnswer();
    long getResult();

}
