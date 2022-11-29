package com.example.surveybackend.services;

import com.example.surveybackend.models.request.PollReplyRequestModel;

public interface PollReplyService {
    public long createPollReply(PollReplyRequestModel model);
}
