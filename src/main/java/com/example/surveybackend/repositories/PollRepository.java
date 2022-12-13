package com.example.surveybackend.repositories;

import com.example.surveybackend.entities.PollEntity;
import com.example.surveybackend.interfaces.PollResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PollRepository extends CrudRepository<PollEntity, Long> {
    public PollEntity findByPollId(String pollId);
    public PollEntity findById(long id);

    public Page<PollEntity> findAllByUserId(Long userId, Pageable pageable);

    public PollEntity findByPollIdAndUserId(String pollId, Long userUd);

    @Query(nativeQuery = true, value = "SELECT q.question_order AS questionOrder, prd.question_id AS questionId, q.content AS question, prd.answer_id AS answerId, a.content AS answer, COUNT(prd.answer_id) AS result FROM poll_replies pr LEFT JOIN poll_reply_details prd ON prd.poll_reply_id = pr.id LEFT JOIN answers a ON a.id = prd.answer_id LEFT JOIN questions q ON q.id = prd.question_id WHERE pr.poll_id = :pollId GROUP BY prd.question_id, prd.answer_id ORDER BY q.question_order ASC")
    public List<PollResult> getPollResults(@Param("pollId") long id);
}
