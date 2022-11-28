package com.example.surveybackend.services;

import com.example.surveybackend.entities.AnswerEntity;
import com.example.surveybackend.entities.PollEntity;
import com.example.surveybackend.entities.QuestionEntity;
import com.example.surveybackend.entities.UserEntity;
import com.example.surveybackend.models.request.PollCreationRequestModel;
import com.example.surveybackend.repositories.PollRepository;
import com.example.surveybackend.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
