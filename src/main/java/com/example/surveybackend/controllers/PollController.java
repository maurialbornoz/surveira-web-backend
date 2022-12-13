package com.example.surveybackend.controllers;

import com.example.surveybackend.entities.PollEntity;
import com.example.surveybackend.interfaces.PollResult;
import com.example.surveybackend.models.request.PollCreationRequestModel;
import com.example.surveybackend.models.responses.*;
import com.example.surveybackend.services.PollService;
import com.example.surveybackend.utils.transformer.PollResultTransformer;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @GetMapping
    public PaginatedPollRest getPolls(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "limit", defaultValue = "10") int limit,
            Authentication authentication){
        Page<PollEntity> paginatedPolls = pollService.getPolls(page, limit, authentication.getPrincipal().toString());
        ModelMapper mapper = new ModelMapper();
//        mapper.typeMap(PollEntity.class, PollRest.class).addMappings(m -> m.skip(PollRest::setQuestions));


        PaginatedPollRest paginatedPollRest = new PaginatedPollRest();
        paginatedPollRest.setPolls(
                paginatedPolls.getContent().stream().map(p -> mapper.map(p, PollRest.class)).collect(Collectors.toList())
        );

        paginatedPollRest.setTotalPages(paginatedPolls.getTotalPages());
        paginatedPollRest.setTotalRecords(paginatedPolls.getTotalElements());
        paginatedPollRest.setCurrentPageRecords(paginatedPolls.getNumberOfElements());

        paginatedPollRest.setCurrentPage(paginatedPolls.getPageable().getPageNumber() + 1);

        return paginatedPollRest;
    }

    @PatchMapping(path = "/{id}")
    public void togglePollOpened(@PathVariable String id, Authentication authentication){
        pollService.togglePollOpened(id, authentication.getPrincipal().toString());
    }

    @DeleteMapping(path = "/{id}")
    public void deletePoll(@PathVariable String id, Authentication authentication){
        pollService.deletePoll(id, authentication.getPrincipal().toString());
    }

    @GetMapping(path = "/{id}/results")
    public PollResultWrapperRest getResults(@PathVariable String id, Authentication authentication){
        List<PollResult> results = pollService.getResults(id, authentication.getPrincipal().toString());
        PollEntity pollEntity = pollService.getPoll(id);
        PollResultTransformer transformer = new PollResultTransformer();

        return new PollResultWrapperRest(transformer.transformData(results), pollEntity.getContent(), pollEntity.getId());
    }
}
