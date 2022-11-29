package com.example.surveybackend.repositories;

import com.example.surveybackend.entities.PollReplyEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PollReplyRepository extends CrudRepository<PollReplyEntity, Long> {
    public List<PollReplyEntity> findAll();
}
