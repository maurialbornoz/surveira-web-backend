package com.example.surveybackend;

import com.example.surveybackend.entities.PollEntity;
import com.example.surveybackend.entities.PollReplyEntity;
import com.example.surveybackend.entities.UserEntity;
import com.example.surveybackend.models.request.PollReplyRequestModel;
import com.example.surveybackend.models.responses.ValidationErrors;
import com.example.surveybackend.repositories.PollReplyRepository;
import com.example.surveybackend.repositories.PollRepository;
import com.example.surveybackend.repositories.UserRepository;
import com.example.surveybackend.services.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PollReplyControllerTests {
    private static final String API_URL = "/polls/reply";

    @Autowired
    TestRestTemplate testRestTemplate;
    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;
    @Autowired
    PollRepository pollRepository;

    @Autowired
    PollReplyRepository pollReplyRepository;

    PollEntity poll;

    @BeforeAll
    public void initializeObjects (){
        UserEntity user = userService.createUser(TestUtil.createValidUser());
        this.poll = pollRepository.save(TestUtil.createValidPollEntity(user));
    }

    @AfterAll
    public void cleanUpAfter(){
        pollRepository.deleteAll();
        userRepository.deleteAll();
    }

    @AfterEach
    public void cleanUp(){
        pollReplyRepository.deleteAll();
    }


    @Test
    public void replyPoll_withoutUser_returnsBadRequest(){
        PollReplyRequestModel model = TestUtil.createValidPollReply(poll);
        model.setUser(null);
        ResponseEntity<ValidationErrors> response = createPollReply(model, ValidationErrors.class);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void replyPoll_withoutUser_returnsUserValidationError(){
        PollReplyRequestModel model = TestUtil.createValidPollReply(poll);
        model.setUser(null);
        ResponseEntity<ValidationErrors> response = createPollReply(model, ValidationErrors.class);
        assertTrue(response.getBody().getErrors().containsKey("user"));
    }

    @Test
    public void replyPoll_withInvalidPollId_returnsBadRequest(){
        PollReplyRequestModel model = TestUtil.createValidPollReply(poll);
        model.setPoll(0L);
        ResponseEntity<ValidationErrors> response = createPollReply(model, ValidationErrors.class);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void replyPoll_withInvalidPollId_returnsPollValidationError(){
        PollReplyRequestModel model = TestUtil.createValidPollReply(poll);
        model.setPoll(0L);
        ResponseEntity<ValidationErrors> response = createPollReply(model, ValidationErrors.class);
        assertTrue(response.getBody().getErrors().containsKey("poll"));
    }

    @Test
    public void replyPoll_withEmptyPollRepliesList_returnsBadRequest(){
        PollReplyRequestModel model = TestUtil.createValidPollReply(poll);
        model.setPollReplies(null);
        ResponseEntity<ValidationErrors> response = createPollReply(model, ValidationErrors.class);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void replyPoll_withEmptyPollRepliesList_returnsPollRepliesValidationError(){
        PollReplyRequestModel model = TestUtil.createValidPollReply(poll);
        model.setPollReplies(null);
        ResponseEntity<ValidationErrors> response = createPollReply(model, ValidationErrors.class);
        assertTrue(response.getBody().getErrors().containsKey("pollReplies"));
    }

    @Test
    public void replyPoll_withInvalidQuestionId_returnsBadRequest(){
        PollReplyRequestModel model = TestUtil.createValidPollReply(poll);
        model.getPollReplies().get(0).setQuestionId(0L);
        ResponseEntity<ValidationErrors> response = createPollReply(model, ValidationErrors.class);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void replyPoll_withInvalidQuestionId_returnsQuestionIdValidationError(){
        PollReplyRequestModel model = TestUtil.createValidPollReply(poll);
        model.getPollReplies().get(0).setQuestionId(0L);
        ResponseEntity<ValidationErrors> response = createPollReply(model, ValidationErrors.class);
        assertTrue(response.getBody().getErrors().containsKey("pollReplies[0].questionId"));
    }

    @Test
    public void replyPoll_withInvalidAnswerId_returnsBadRequest(){
        PollReplyRequestModel model = TestUtil.createValidPollReply(poll);
        model.getPollReplies().get(0).setAnswerId(0L);
        ResponseEntity<ValidationErrors> response = createPollReply(model, ValidationErrors.class);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void replyPoll_withInvalidAnswerId_returnsAnswerIdValidationError(){
        PollReplyRequestModel model = TestUtil.createValidPollReply(poll);
        model.getPollReplies().get(0).setAnswerId(0L);
        ResponseEntity<ValidationErrors> response = createPollReply(model, ValidationErrors.class);
        assertTrue(response.getBody().getErrors().containsKey("pollReplies[0].answerId"));
    }

    @Test
    public void replyPoll_withValidData_returnsOk(){
        PollReplyRequestModel model = TestUtil.createValidPollReply(poll);
        ResponseEntity<Object> response = createPollReply(model, Object.class);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void replyPoll_withValidData_savedInDB(){
        PollReplyRequestModel model = TestUtil.createValidPollReply(poll);
        createPollReply(model, Object.class);
        List<PollReplyEntity> replies = pollReplyRepository.findAll();
        assertEquals(replies.size(), 1);
    }





    public <T> ResponseEntity<T> createPollReply(PollReplyRequestModel model, Class<T> responseType){
        return testRestTemplate.postForEntity(API_URL, model, responseType);
    }
}
