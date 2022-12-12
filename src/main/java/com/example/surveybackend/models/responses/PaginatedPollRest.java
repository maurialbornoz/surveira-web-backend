package com.example.surveybackend.models.responses;

import lombok.Data;

import java.util.List;

@Data
public class PaginatedPollRest {
    private List<PollRest> polls;
    private int totalPages;
    private long totalRecords;
    private long currentPageRecords;
    private int currentPage;
}
