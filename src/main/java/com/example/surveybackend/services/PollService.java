package com.example.surveybackend.services;

import com.example.surveybackend.models.request.PollCreationRequestModel;

public interface PollService {
    public String createPoll(PollCreationRequestModel model, String email);
}
