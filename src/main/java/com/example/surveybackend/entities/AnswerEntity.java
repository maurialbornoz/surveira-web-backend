package com.example.surveybackend.entities;

import lombok.Data;

import javax.persistence.*;

@Entity(name = "answers")
@Data
public class AnswerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String content;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private QuestionEntity question;
}
