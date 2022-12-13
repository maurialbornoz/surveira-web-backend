package com.example.surveybackend.utils.transformer;

import com.example.surveybackend.interfaces.PollResult;
import com.example.surveybackend.models.responses.PollRest;
import com.example.surveybackend.models.responses.PollResultRest;
import com.example.surveybackend.models.responses.ResultsDetailRest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PollResultTransformer implements Transformer<List<PollResult>, List<PollResultRest>>{


    @Override
    public List<PollResultRest> transformData(List<PollResult> data) {
        Map<String, PollResultRest> transformedData = new HashMap<String, PollResultRest>();
        for (PollResult result: data){
            PollResultRest pollResultRest;

            String key = Long.toString(result.getQuestionId());
            if(!transformedData.containsKey(Long.toString(result.getQuestionId()))){
                List<ResultsDetailRest> details = new ArrayList<ResultsDetailRest>();
                details.add(new ResultsDetailRest(result.getAnswer(), result.getResult()));
                pollResultRest = new PollResultRest(result.getQuestion(), details);
                transformedData.put(key, pollResultRest);
            } else {
                pollResultRest = transformedData.get(key);
                pollResultRest.getDetails().add(new ResultsDetailRest(result.getAnswer(), result.getResult()));
            }
        }
        List<PollResultRest> resultList = new ArrayList<>(transformedData.values());
        return resultList;
    }
}
