package com.example.surveybackend.services;

import com.example.surveybackend.entities.AnswerEntity;
import com.example.surveybackend.entities.PollEntity;
import com.example.surveybackend.entities.QuestionEntity;
import com.example.surveybackend.entities.UserEntity;
import com.example.surveybackend.interfaces.PollResult;
import com.example.surveybackend.models.request.PollCreationRequestModel;
import com.example.surveybackend.repositories.PollRepository;
import com.example.surveybackend.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PollServiceImpl implements PollService{

    PollRepository pollRepository;
    UserRepository userRepository;

    public PollServiceImpl(PollRepository pollRepository, UserRepository userRepository){
        this.pollRepository = pollRepository;
        this.userRepository = userRepository;
    }
    @Override
    public String createPoll(PollCreationRequestModel model, String email) {
        UserEntity user = userRepository.findByEmail(email);
        ModelMapper mapper  = new ModelMapper();
        PollEntity pollEntity = mapper.map(model, PollEntity.class);
        pollEntity.setUser(user);
        pollEntity.setPollId(UUID.randomUUID().toString());

        for (QuestionEntity question: pollEntity.getQuestions()){
            question.setPoll(pollEntity);
            for (AnswerEntity answer: question.getAnswers()){
                answer.setQuestion(question);
            }
        }
        pollRepository.save(pollEntity);

        return pollEntity.getPollId();
    }

    @Override
    public PollEntity getPoll(String pollId) {
        PollEntity poll = pollRepository.findByPollId(pollId);
        if(poll == null) {
            throw new RuntimeException("Poll not found");
        }
        if(!poll.isOpened()){
            throw new RuntimeException("Poll does not accept more replies");

        }
        return poll;
    }

    @Override
    public Page<PollEntity> getPolls(int page, int limit, String email) {
        UserEntity user = userRepository.findByEmail(email);
        Pageable pageable = PageRequest.of(page, limit);
        Page<PollEntity> paginatedPolls = this.pollRepository.findAllByUserId(user.getId(), pageable);
        return paginatedPolls;
    }

    @Override
    public void togglePollOpened(String pollId, String email) {
        UserEntity user = userRepository.findByEmail(email);
        PollEntity poll = pollRepository.findByPollIdAndUserId(pollId, user.getId());
        if(poll == null){
            throw new RuntimeException("Poll not founded");
        }

        poll.setOpened(!poll.isOpened());
        pollRepository.save(poll);
    }

    @Override
    public void deletePoll(String pollId, String email) {
        UserEntity user = userRepository.findByEmail(email);
        PollEntity poll = pollRepository.findByPollIdAndUserId(pollId, user.getId());
        if(poll == null){
            throw new RuntimeException("Poll not founded");
        }

        pollRepository.delete(poll);
    }

    @Override
    public List<PollResult> getResults(String pollId, String email) {
        UserEntity user = userRepository.findByEmail(email);
        PollEntity poll = pollRepository.findByPollIdAndUserId(pollId, user.getId());
        if(poll == null){
            throw new RuntimeException("Poll not founded");
        }
        return pollRepository.getPollResults(poll.getId());
    }
}
