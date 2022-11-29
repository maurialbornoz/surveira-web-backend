package com.example.surveybackend.services;

import com.example.surveybackend.entities.PollEntity;
import com.example.surveybackend.entities.PollReplyDetailEntity;
import com.example.surveybackend.entities.PollReplyEntity;
import com.example.surveybackend.models.request.PollReplyRequestModel;
import com.example.surveybackend.repositories.PollReplyRepository;
import com.example.surveybackend.repositories.PollRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;


import java.util.HashSet;
import java.util.Set;

@Service
public class PollReplyServiceImpl implements PollReplyService{

    PollReplyRepository pollReplyRepository;
    PollRepository pollRepository;

    public PollReplyServiceImpl(PollReplyRepository pollReplyRepository, PollRepository pollRepository){
        this.pollReplyRepository = pollReplyRepository;
        this.pollRepository = pollRepository;
    }

    @Override
    public long createPollReply(PollReplyRequestModel model) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setAmbiguityIgnored(true);
        PollReplyEntity pollReply = mapper.map(model, PollReplyEntity.class);
        PollEntity poll = pollRepository.findById((long)model.getPoll());
        pollReply.setPoll(poll);

        Set<Long> uniqueReplies = new HashSet<>();
        for (PollReplyDetailEntity pollReplyDetailEntity: pollReply.getPollReplies()){
            pollReplyDetailEntity.setPollReply(pollReply);
            uniqueReplies.add(pollReplyDetailEntity.getQuestionId());
        }
        if(uniqueReplies.size() != poll.getQuestions().size()){
            throw new RuntimeException("You should answer all the questions");
        }
        PollReplyEntity replyEntity = pollReplyRepository.save(pollReply);
        return replyEntity.getId();
    }
}
