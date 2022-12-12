package com.example.surveybackend.repositories;

import com.example.surveybackend.entities.PollEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PollRepository extends CrudRepository<PollEntity, Long> {
    public PollEntity findByPollId(String pollId);
    public PollEntity findById(long id);

    public Page<PollEntity> findAllByUserId(Long userId, Pageable pageable);

    public PollEntity findByPollIdAndUserId(String pollId, Long userUd);
}
