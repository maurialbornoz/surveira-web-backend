package com.example.surveybackend;

import com.example.surveybackend.entities.PollEntity;
import com.example.surveybackend.entities.UserEntity;
import com.example.surveybackend.models.request.PollCreationRequestModel;
import com.example.surveybackend.models.request.UserLoginRequestModel;
import com.example.surveybackend.models.request.UserRegisterRequestModel;
import com.example.surveybackend.models.responses.PaginatedPollRest;
import com.example.surveybackend.models.responses.PollRest;
import com.example.surveybackend.models.responses.PollResultWrapperRest;
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
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
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
        testRestTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
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

    //<editor-fold desc="create poll">
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

    //</editor-fold desc="create poll">

    //<editor-fold desc="get polls with questions">
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
    //</editor-fold>


    //<editor-fold desc="get poll with questions">
    @Test
    public void getPolls_withoutAuthentication_returnsForbidden(){
        ResponseEntity<Object> response = getPolls(API_URL, false, new ParameterizedTypeReference<Object>() {});
        assertEquals(response.getStatusCode(), HttpStatus.FORBIDDEN);
    }

    @Test
    public void getPolls_withAuthentication_returnsOk(){
        ResponseEntity<Object> response = getPolls(API_URL, true, new ParameterizedTypeReference<Object>() {});
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void getPolls_withAuthentication_returnsPaginatedPollRest(){
        pollRepository.save(TestUtil.createValidPollEntity(user));
        ResponseEntity<PaginatedPollRest> response = getPolls(API_URL, true, new ParameterizedTypeReference<PaginatedPollRest>() {});
        List<PollRest> polls = response.getBody().getPolls();
        assertEquals(polls.size(), 1);
    }

    @Test
    public void getPolls_withAuthentication_returnsPaginationData(){
        pollRepository.save(TestUtil.createValidPollEntity(user));
        ResponseEntity<PaginatedPollRest> response = getPolls(API_URL, true, new ParameterizedTypeReference<PaginatedPollRest>() {});
        assertEquals(response.getBody().getCurrentPage(), 1);
        assertEquals(response.getBody().getCurrentPageRecords(), 1);
        assertEquals(response.getBody().getTotalPages(), 1);
        assertEquals(response.getBody().getTotalPages(), 1);

    }

    @Test
    public void getPolls_withAuthenticationWithLimitParameter_returnsLimitedPolls(){
        pollRepository.save(TestUtil.createValidPollEntity(user));
        pollRepository.save(TestUtil.createValidPollEntity(user));
        pollRepository.save(TestUtil.createValidPollEntity(user));

        ResponseEntity<PaginatedPollRest> response = getPolls(true, new ParameterizedTypeReference<PaginatedPollRest>() {},2 );
        List<PollRest> polls = response.getBody().getPolls();

        assertEquals(polls.size(), 2);

    }

    @Test
    public void getPolls_withAuthenticationWithPageParameter_returnsSecondPagePolls(){
        pollRepository.save(TestUtil.createValidPollEntity(user));
        pollRepository.save(TestUtil.createValidPollEntity(user));
        pollRepository.save(TestUtil.createValidPollEntity(user));

        ResponseEntity<PaginatedPollRest> response = getPolls(true, new ParameterizedTypeReference<PaginatedPollRest>() {},1, 2 );
        List<PollRest> polls = response.getBody().getPolls();

        assertEquals(polls.size(), 1);

    }

    //</editor-fold>

    //<editor-fold desc="toggle poll opened">
    @Test
    public void togglePollOpened_withoutAuthentication_returnsForbidden(){
        ResponseEntity<Object> response = togglePollOpened(false, "abc", new ParameterizedTypeReference<Object>() {});
        assertEquals(response.getStatusCode(), HttpStatus.FORBIDDEN);
    }

    @Test
    public void togglePollOpened_withAuthenticationToAPollThatDoesNotBelongToTheUser_returnsInternalServerError(){
        UserEntity otherUser = userService.createUser(TestUtil.createValidUser());
        PollEntity poll = pollRepository.save(TestUtil.createValidPollEntity(otherUser));
        ResponseEntity<Object> response = togglePollOpened(true, poll.getPollId(), new ParameterizedTypeReference<Object>() {});
        assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void togglePollOpened_withAuthenticationToAnExistingPollBelongingToTheUser_returnsOk(){
        PollEntity poll = pollRepository.save(TestUtil.createValidPollEntity(user));
        ResponseEntity<Object> response = togglePollOpened(true, poll.getPollId(), new ParameterizedTypeReference<Object>() {});
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void togglePollOpened_withAuthenticationToAnExistingPollBelongingToTheUser_changesOpenedInBD(){
        PollEntity poll = pollRepository.save(TestUtil.createValidPollEntity(user));
        togglePollOpened(true, poll.getPollId(), new ParameterizedTypeReference<Object>() {});
        PollEntity updatedPoll = pollRepository.findById((long)poll.getId());
        assertEquals(updatedPoll.isOpened(), false);
    }
    //</editor-fold>

    //<editor-fold desc="delete poll">
    @Test
    public void deletePoll_withoutAuthentication_returnsForbidden(){
        ResponseEntity<Object> response = deletePoll(false, "abc", new ParameterizedTypeReference<Object>() {});
        assertEquals(response.getStatusCode(), HttpStatus.FORBIDDEN);
    }

    @Test
    public void deletePoll_withAuthenticationToAPollThatDoesNotBelongToTheUser_returnsInternalServerError(){
        UserEntity otherUser = userService.createUser(TestUtil.createValidUser());
        PollEntity poll = pollRepository.save(TestUtil.createValidPollEntity(otherUser));
        ResponseEntity<Object> response = deletePoll(true, poll.getPollId(), new ParameterizedTypeReference<Object>() {});
        assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void deletePoll_withAuthenticationToAnExistingPollBelongingToTheUser_returnsOk(){
        PollEntity poll = pollRepository.save(TestUtil.createValidPollEntity(user));
        ResponseEntity<Object> response = deletePoll(true, poll.getPollId(), new ParameterizedTypeReference<Object>() {});
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void deletePoll_withAuthenticationToAnExistingPollBelongingToTheUser_deletePollInBD(){
        PollEntity poll = pollRepository.save(TestUtil.createValidPollEntity(user));
        deletePoll(true, poll.getPollId(), new ParameterizedTypeReference<Object>() {});
        PollEntity deletedPoll = pollRepository.findById((long)poll.getId());
        assertNull(deletedPoll);
    }
    //</editor-fold>


    //<editor-fold desc="get results">
    @Test
    public void getResults_withoutAuthentication_returnsForbidden(){
        ResponseEntity<Object> response = getResults(false, "abc", new ParameterizedTypeReference<Object>() {});
        assertEquals(response.getStatusCode(), HttpStatus.FORBIDDEN);
    }

    @Test
    public void getResults_withAuthenticationToAPollThatDoesNotExist_returnsInternalServerError(){
        PollEntity poll = pollRepository.save(TestUtil.createValidPollEntity(user));
        ResponseEntity<Object> response = getResults(true, "nonexistent_id", new ParameterizedTypeReference<Object>() {});
        assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void getResults_withAuthenticationToAPollThatDoesNotBelongToTheUser_returnsInternalServerError(){
        UserEntity otherUser = userService.createUser(TestUtil.createValidUser());
        PollEntity poll = pollRepository.save(TestUtil.createValidPollEntity(otherUser));
        ResponseEntity<Object> response = getResults(true, poll.getPollId(), new ParameterizedTypeReference<Object>() {});
        assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @Test
    public void getResults_withAuthenticationToAnExistingPollBelongingToTheUser_returnsOk(){
        PollEntity poll = pollRepository.save(TestUtil.createValidPollEntity(user));
        ResponseEntity<Object> response = getResults(true, poll.getPollId(), new ParameterizedTypeReference<Object>() {});
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void getResults_withAuthenticationToAnExistingPollBelongingToTheUser_returnsPollResultWrapperRest(){
        PollEntity poll = pollRepository.save(TestUtil.createValidPollEntity(user));
        ResponseEntity<PollResultWrapperRest> response = getResults(true, poll.getPollId(), new ParameterizedTypeReference<PollResultWrapperRest>() {});
        assertEquals(poll.getContent(), response.getBody().getContent());
    }
    //</editor-fold>

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

    public <T> ResponseEntity<T> getPolls(boolean auth, ParameterizedTypeReference<T> responseType, int limit) {
        String url = API_URL + "?limit=" + limit;
        return getPolls(url, auth, responseType);
    }
    public <T> ResponseEntity<T> getPolls(boolean auth, ParameterizedTypeReference<T> responseType, int page, int limit) {
        String url = API_URL + "?limit=" + limit + "&page=" + page;
        return getPolls(url, auth, responseType);
    }

    public <T> ResponseEntity<T> getPolls(String url, boolean auth, ParameterizedTypeReference<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        if(auth){
            headers.setBearerAuth(token);
        }
        HttpEntity<Object> entity = new HttpEntity<Object>(null, headers);
        return testRestTemplate.exchange(url, HttpMethod.GET, entity, responseType);
    }

    public <T> ResponseEntity<T> togglePollOpened(boolean auth, String pollId, ParameterizedTypeReference<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        if(auth){
            headers.setBearerAuth(token);
        }
        HttpEntity<Object> entity = new HttpEntity<Object>(null, headers);
        String url = API_URL + "/" + pollId;
        return testRestTemplate.exchange(url, HttpMethod.PATCH, entity, responseType);
    }

    public <T> ResponseEntity<T> deletePoll(boolean auth, String pollId, ParameterizedTypeReference<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        if(auth){
            headers.setBearerAuth(token);
        }
        HttpEntity<Object> entity = new HttpEntity<Object>(null, headers);
        String url = API_URL + "/" + pollId;
        return testRestTemplate.exchange(url, HttpMethod.DELETE, entity, responseType);
    }

    public <T> ResponseEntity<T> getResults(boolean auth, String pollId, ParameterizedTypeReference<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        if(auth){
            headers.setBearerAuth(token);
        }
        HttpEntity<Object> entity = new HttpEntity<Object>(null, headers);
        String url = API_URL + "/" + pollId + "/results";
        return testRestTemplate.exchange(url, HttpMethod.GET, entity, responseType);
    }

    public <T> ResponseEntity<T> createPoll(PollCreationRequestModel model, Class<T> responseType){
        return testRestTemplate.postForEntity(API_URL, model, responseType);
    }

    public <T> ResponseEntity<T> getPollWithQuestions(String url, Class<T> responseType){
        return testRestTemplate.getForEntity(url, responseType);
    }
}
