package com.example.surveybackend.controllers;

import com.example.surveybackend.entities.PollEntity;
import com.example.surveybackend.models.request.PollCreationRequestModel;
import com.example.surveybackend.models.responses.CreatedPollRest;
import com.example.surveybackend.models.responses.PollRest;
import com.example.surveybackend.services.PollService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/polls")
public class PollController {
    @Autowired
    PollService pollService;
    @PostMapping
    public CreatedPollRest createPoll(@RequestBody @Valid PollCreationRequestModel pollCreationRequestModel, Authentication authentication){
        String pollId = pollService.createPoll(pollCreationRequestModel, authentication.getPrincipal().toString());
        return new CreatedPollRest(pollId);
    }

    @GetMapping(path = "{id}/questions")
    public PollRest getPollWithQuestions(@PathVariable String id){
        PollEntity poll = pollService.getPoll(id);
        ModelMapper mapper = new ModelMapper();
        return mapper.map(poll, PollRest.class);
    }
}
