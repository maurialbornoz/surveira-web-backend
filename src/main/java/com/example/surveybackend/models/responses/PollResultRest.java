package com.example.surveybackend.models.responses;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PollResultRest {
    private String question;
    private List<ResultsDetailRest> details;

}
