package com.example.surveybackend.controllers;

import com.example.surveybackend.models.request.PollReplyDetailRequestModel;
import com.example.surveybackend.models.request.PollReplyRequestModel;
import com.example.surveybackend.models.responses.CreatedPollReplyRest;
import com.example.surveybackend.services.PollReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/polls/reply")
public class PollReplyController {

    @Autowired
    PollReplyService pollReplyService;


    @PostMapping
    public CreatedPollReplyRest replyPoll(@RequestBody @Valid PollReplyRequestModel model){
        return new CreatedPollReplyRest(pollReplyService.createPollReply(model));
    }
}
