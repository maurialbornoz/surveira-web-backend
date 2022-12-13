package com.example.surveybackend.services;

import com.example.surveybackend.entities.PollEntity;
import com.example.surveybackend.interfaces.PollResult;
import com.example.surveybackend.models.request.PollCreationRequestModel;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PollService {
    public String createPoll(PollCreationRequestModel model, String email);

    public PollEntity getPoll(String pollId);
    public Page<PollEntity> getPolls(int page, int limit, String email);

    public void togglePollOpened(String pollId, String email);
    public void deletePoll(String pollId, String email);

    public List<PollResult> getResults(String pollId, String email);
}
