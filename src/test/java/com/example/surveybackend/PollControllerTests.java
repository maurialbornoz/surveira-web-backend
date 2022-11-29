package com.example.surveybackend;

import com.example.surveybackend.entities.PollEntity;
import com.example.surveybackend.entities.UserEntity;
import com.example.surveybackend.models.request.PollCreationRequestModel;
import com.example.surveybackend.models.request.UserLoginRequestModel;
import com.example.surveybackend.models.request.UserRegisterRequestModel;
import com.example.surveybackend.models.responses.PollRest;
import com.example.surveybackend.models.responses.ValidationErrors;
import com.example.surveybackend.repositories.PollRepository;
import com.example.surveybackend.repositories.UserRepository;
import com.example.surveybackend.services.PollService;
import com.example.surveybackend.services.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PollControllerTests {
    private static final String API_URL = "/polls";
    private static final String API_LOGIN_URL = "/users/login";
    private String token = "";

    private UserEntity user = null;

    @Autowired
    TestRestTemplate testRestTemplate;
    @Autowired
    UserService userService;

    @Autowired
    PollService pollService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PollRepository pollRepository;

    @BeforeAll
    public void initializeObjects(){
        UserRegisterRequestModel user = TestUtil.createValidUser();
        // insert user en h2 db
        this.user = userService.createUser(user);
        UserLoginRequestModel model = new UserLoginRequestModel();
        model.setEmail(user.getEmail());
        model.setPassword(user.getPassword());
        ResponseEntity<Map<String, String>> response = login(model, new ParameterizedTypeReference<Map<String, String>>(){});
        Map<String, String> body = response.getBody();
        this.token = body.get("token");
    }

    @AfterEach
    public void cleanUp(){
        pollRepository.deleteAll();
    }

    @AfterAll
    public void cleanUpAfter(){
        userRepository.deleteAll();
    }

    @Test
    public void createPoll_withoutAuthentication_returnsForbidden(){
        ResponseEntity<Object> response = createPoll(new PollCreationRequestModel(), Object.class);
        assertEquals(response.getStatusCode(), HttpStatus.FORBIDDEN);
    }

    @Test
    public void createPoll_withAuthenticationWithoutData_returnsBadRequest(){
        ResponseEntity<ValidationErrors> response = createPoll(new PollCreationRequestModel(), new ParameterizedTypeReference<ValidationErrors>(){});
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void createPoll_withAuthenticationWithoutPollContent_returnsBadRequest(){
        PollCreationRequestModel poll = TestUtil.createValidPoll();
        poll.setContent("");
        ResponseEntity<ValidationErrors> response = createPoll(poll, new ParameterizedTypeReference<ValidationErrors>(){});
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void createPoll_withAuthenticationWithoutPollContent_returnsContentValidationError(){
        PollCreationRequestModel poll = TestUtil.createValidPoll();
        poll.setContent("");
        ResponseEntity<ValidationErrors> response = createPoll(poll, new ParameterizedTypeReference<ValidationErrors>(){});
        assertTrue(response.getBody().getErrors().containsKey("content"));
    }

    @Test
    public void createPoll_withAuthenticationWithoutPollQuestions_returnsBadRequest(){
        PollCreationRequestModel poll = TestUtil.createValidPoll();
        poll.setQuestions(null);
        ResponseEntity<ValidationErrors> response = createPoll(poll, new ParameterizedTypeReference<ValidationErrors>(){});
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void createPoll_withAuthenticationWithoutPollQuestions_returnsPollQuestionValidationError(){
        PollCreationRequestModel poll = TestUtil.createValidPoll();
        poll.setQuestions(null);
        ResponseEntity<ValidationErrors> response = createPoll(poll, new ParameterizedTypeReference<ValidationErrors>(){});
        assertTrue(response.getBody().getErrors().containsKey("questions"));
    }

    @Test
    public void createPoll_withAuthenticationWithValidQuestionWithoutContent_returnsBadRequest(){
        PollCreationRequestModel poll = TestUtil.createValidPoll();
        poll.getQuestions().get(0).setContent("");
        ResponseEntity<ValidationErrors> response = createPoll(poll, new ParameterizedTypeReference<ValidationErrors>(){});
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void createPoll_withAuthenticationWithValidQuestionWithoutContent_returnsQuestionContentValidationError(){
        PollCreationRequestModel poll = TestUtil.createValidPoll();
        poll.getQuestions().get(0).setContent("");
        ResponseEntity<ValidationErrors> response = createPoll(poll, new ParameterizedTypeReference<ValidationErrors>(){});
        assertTrue(response.getBody().getErrors().containsKey("questions[0].content"));
    }

    @Test
    public void createPoll_withAuthenticationWithValidQuestionWithIncorrectOrder_returnsBadRequest(){
        PollCreationRequestModel poll = TestUtil.createValidPoll();
        poll.getQuestions().get(0).setQuestionOrder(0);
        ResponseEntity<ValidationErrors> response = createPoll(poll, new ParameterizedTypeReference<ValidationErrors>(){});
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void createPoll_withAuthenticationWithValidQuestionWithIncorrectOrder_returnsQuestionOrderValidationError(){
        PollCreationRequestModel poll = TestUtil.createValidPoll();
        poll.getQuestions().get(0).setQuestionOrder(0);
        ResponseEntity<ValidationErrors> response = createPoll(poll, new ParameterizedTypeReference<ValidationErrors>(){});
        assertTrue(response.getBody().getErrors().containsKey("questions[0].questionOrder"));
    }

    @Test
    public void createPoll_withAuthenticationWithValidQuestionWithIncorrectQuestionType_returnsBadRequest(){
        PollCreationRequestModel poll = TestUtil.createValidPoll();
        poll.getQuestions().get(0).setType(TestUtil.generateRandomString(10));
        ResponseEntity<ValidationErrors> response = createPoll(poll, new ParameterizedTypeReference<ValidationErrors>(){});
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void createPoll_withAuthenticationWithValidQuestionWithIncorrectQuestionType_returnsQuestionTypeValidationError(){
        PollCreationRequestModel poll = TestUtil.createValidPoll();
        poll.getQuestions().get(0).setType(TestUtil.generateRandomString(10));
        ResponseEntity<ValidationErrors> response = createPoll(poll, new ParameterizedTypeReference<ValidationErrors>(){});
        assertTrue(response.getBody().getErrors().containsKey("questions[0].type"));
    }

    @Test
    public void createPoll_withAuthenticationWithValidQuestionWithoutAnswers_returnsBadRequest(){
        PollCreationRequestModel poll = TestUtil.createValidPoll();
        poll.getQuestions().get(0).setAnswers(null);
        ResponseEntity<ValidationErrors> response = createPoll(poll, new ParameterizedTypeReference<ValidationErrors>(){});
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void createPoll_withAuthenticationWithValidQuestionWithoutAnswers_returnsAnswerValidationError(){
        PollCreationRequestModel poll = TestUtil.createValidPoll();
        poll.getQuestions().get(0).setAnswers(null);
        ResponseEntity<ValidationErrors> response = createPoll(poll, new ParameterizedTypeReference<ValidationErrors>(){});
        assertTrue(response.getBody().getErrors().containsKey("questions[0].answers"));
    }

    @Test
    public void createPoll_withAuthenticationWithoutAnswerContent_returnsBadRequest(){
        PollCreationRequestModel poll = TestUtil.createValidPoll();
        poll.getQuestions().get(0).getAnswers().get(0).setContent("");
        ResponseEntity<ValidationErrors> response = createPoll(poll, new ParameterizedTypeReference<ValidationErrors>(){});
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void createPoll_withAuthenticationWithoutAnswerContent_returnsAnswerContentValidationError(){
        PollCreationRequestModel poll = TestUtil.createValidPoll();
        poll.getQuestions().get(0).getAnswers().get(0).setContent("");
        ResponseEntity<ValidationErrors> response = createPoll(poll, new ParameterizedTypeReference<ValidationErrors>(){});
        assertTrue(response.getBody().getErrors().containsKey("questions[0].answers[0].content"));
    }

    @Test
    public void createPoll_withValidPoll_returnsOk(){
        PollCreationRequestModel poll = TestUtil.createValidPoll();
        ResponseEntity<Object> response = createPoll(poll, new ParameterizedTypeReference<Object>(){});
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void createPoll_withValidPoll_returnsPollId(){
        PollCreationRequestModel poll = TestUtil.createValidPoll();
        ResponseEntity<Map<String, String>> response = createPoll(poll, new ParameterizedTypeReference<Map<String, String>>(){});
        assertTrue(response.getBody().containsKey("pollId"));
    }

    @Test
    public void createPoll_withValidPoll_savedInDB(){
        PollCreationRequestModel poll = TestUtil.createValidPoll();
        ResponseEntity<Map<String, String>> response = createPoll(poll, new ParameterizedTypeReference<Map<String, String>>(){});
        PollEntity pollDB = pollRepository.findByPollId(response.getBody().get("pollId"));
        assertNotNull(pollDB);
    }
    @Test
    public void getPollWithQuestions_withNonExistentPollInDB_returnsInternalServerError(){
        ResponseEntity<Object> response = getPollWithQuestions(API_URL + "/uuid/questions", Object.class);
        assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void getPollWithQuestions_withExistentPollInDB_returnsOk(){
        PollCreationRequestModel model = TestUtil.createValidPoll();
        String uuid = pollService.createPoll(model, user.getEmail());
        ResponseEntity<Object> response = getPollWithQuestions(API_URL + "/" + uuid + "/questions", Object.class);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void getPollWithQuestions_withExistentPollInDB_returnsPollRest(){
        PollCreationRequestModel model = TestUtil.createValidPoll();
        String uuid = pollService.createPoll(model, user.getEmail());
        ResponseEntity<PollRest> response = getPollWithQuestions(API_URL + "/" + uuid + "/questions", PollRest.class);
        assertEquals(uuid, response.getBody().getPollId());
    }





    public <T> ResponseEntity<T> login(UserLoginRequestModel model, ParameterizedTypeReference responseType){
        HttpEntity<UserLoginRequestModel> entity = new HttpEntity<UserLoginRequestModel>(model, new HttpHeaders());
        return testRestTemplate.exchange(API_LOGIN_URL, HttpMethod.POST, entity, responseType);
    }

    public <T> ResponseEntity<T> createPoll(PollCreationRequestModel data, ParameterizedTypeReference<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<PollCreationRequestModel> entity = new HttpEntity<PollCreationRequestModel>(data, headers);
        return testRestTemplate.exchange(API_URL, HttpMethod.POST, entity, responseType);
    }

    public <T> ResponseEntity<T> createPoll(PollCreationRequestModel model, Class<T> responseType){
        return testRestTemplate.postForEntity(API_URL, model, responseType);
    }

    public <T> ResponseEntity<T> getPollWithQuestions(String url, Class<T> responseType){
        return testRestTemplate.getForEntity(url, responseType);
    }
}
