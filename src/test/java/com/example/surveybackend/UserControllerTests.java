package com.example.surveybackend;

import com.example.surveybackend.entities.UserEntity;
import com.example.surveybackend.models.request.UserLoginRequestModel;
import com.example.surveybackend.models.request.UserRegisterRequestModel;
import com.example.surveybackend.models.responses.UserRest;
import com.example.surveybackend.models.responses.ValidationErrors;
import com.example.surveybackend.repositories.UserRepository;
import com.example.surveybackend.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserControllerTests {
    private static final String API_REGISTER_URL = "/users";
    private static final String API_LOGIN_URL = "/users/login";
    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    public void cleanup(){
        userRepository.deleteAll();
    }

    @Test
    public void createUser_withoutData_returnsBadRequest(){
        ResponseEntity<Object> response = register(new UserRegisterRequestModel(), Object.class);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void createUser_withoutNameField_returnsBadRequest(){
        UserRegisterRequestModel user = TestUtil.createValidUser();
        user.setName(null);
        ResponseEntity<Object> response = register(user, Object.class);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void createUser_withoutPasswordField_returnsBadRequest(){
        UserRegisterRequestModel user = TestUtil.createValidUser();
        user.setPassword(null);
        ResponseEntity<Object> response = register(user, Object.class);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void createUser_withoutEmailField_returnsBadRequest(){
        UserRegisterRequestModel user = TestUtil.createValidUser();
        user.setEmail(null);
        ResponseEntity<Object> response = register(user, Object.class);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void createUser_withoutData_returnsValidationErrors(){
        ResponseEntity<ValidationErrors> response = register(new UserRegisterRequestModel(), ValidationErrors.class);
        Map<String, String> errors = response.getBody().getErrors();
        assertEquals(errors.size(), 3);
    }

    @Test
    public void createUser_withoutNameField_returnsNameValidationError(){
        UserRegisterRequestModel user = TestUtil.createValidUser();
        user.setName(null);
        ResponseEntity<ValidationErrors> response = register(user, ValidationErrors.class);
        Map<String, String> errors = response.getBody().getErrors();
        assertTrue(errors.containsKey("name"));
    }

    @Test
    public void createUser_withoutEmailField_returnsEmailValidationError(){
        UserRegisterRequestModel user = TestUtil.createValidUser();
        user.setEmail(null);
        ResponseEntity<ValidationErrors> response = register(user, ValidationErrors.class);
        Map<String, String> errors = response.getBody().getErrors();
        assertTrue(errors.containsKey("email"));
    }

    @Test
    public void createUser_withoutPasswordField_returnsPasswordValidationError(){
        UserRegisterRequestModel user = TestUtil.createValidUser();
        user.setPassword(null);
        ResponseEntity<ValidationErrors> response = register(user, ValidationErrors.class);
        Map<String, String> errors = response.getBody().getErrors();
        assertTrue(errors.containsKey("password"));
    }

    @Test
    public void createUser_withValidUser_returnsOk(){
        UserRegisterRequestModel user = TestUtil.createValidUser();
        ResponseEntity<UserRest> response = register(user, UserRest.class);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void createUser_withValidUser_returnsUserRest(){
        UserRegisterRequestModel user = TestUtil.createValidUser();
        ResponseEntity<UserRest> response = register(user, UserRest.class);
        assertEquals(response.getBody().getName(), user.getName());
    }

    @Test
    public void createUser_withValidUser_savedInDB(){
        UserRegisterRequestModel user = TestUtil.createValidUser();
        ResponseEntity<UserRest> response = register(user, UserRest.class);
        UserEntity userDB = userRepository.findById((long)response.getBody().getId());
        assertNotNull(userDB);
    }

    @Test
    public void createUser_withValidUser_hashedPasswordSavedInDB(){
        UserRegisterRequestModel user = TestUtil.createValidUser();
        ResponseEntity<UserRest> response = register(user, UserRest.class);
        UserEntity userDB = userRepository.findById((long)response.getBody().getId());
        assertNotEquals(user.getPassword(), userDB.getEncryptedPassword());
    }

    @Test
    public void createUser_withEmailAlreadyTaken_returnsBadRequest(){
        UserRegisterRequestModel user = TestUtil.createValidUser();
        register(user, UserRest.class);
        ResponseEntity<UserRest> secondResponse = register(user, UserRest.class);
        assertEquals(secondResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void createUser_withEmailAlreadyTaken_returnsEmailValidationError(){
        UserRegisterRequestModel user = TestUtil.createValidUser();
        register(user, UserRest.class);
        ResponseEntity<ValidationErrors> secondResponse = register(user, ValidationErrors.class);
        Map<String, String> errors = secondResponse.getBody().getErrors();
        assertTrue(errors.containsKey("email"));
    }


    @Test
    public void getUser_withoutToken_returnsForbidden(){
        ResponseEntity<Object> response = getUser(null, new ParameterizedTypeReference<Object>(){});
        assertEquals(response.getStatusCode(), HttpStatus.FORBIDDEN);
    }

    @Test
    public void getUser_withToken_returnsOk(){
        UserRegisterRequestModel user = TestUtil.createValidUser();
        // insert user en h2 db
        userService.createUser(user);
        UserLoginRequestModel model = new UserLoginRequestModel();
        model.setEmail(user.getEmail());
        model.setPassword(user.getPassword());

        ResponseEntity<Map<String, String>> responseLogin = login(model, new ParameterizedTypeReference<Map<String, String>>(){});
        Map<String, String> body = responseLogin.getBody();
        String token = body.get("token").replace("Bearer", "");
        ResponseEntity<UserRest> response = getUser(token, new ParameterizedTypeReference<UserRest>(){});
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void getUser_withToken_returnsUserRest(){
        UserRegisterRequestModel user = TestUtil.createValidUser();
        // insert user en h2 db
        userService.createUser(user);
        UserLoginRequestModel model = new UserLoginRequestModel();
        model.setEmail(user.getEmail());
        model.setPassword(user.getPassword());

        ResponseEntity<Map<String, String>> responseLogin = login(model, new ParameterizedTypeReference<Map<String, String>>(){});
        Map<String, String> body = responseLogin.getBody();
        String token = body.get("token").replace("Bearer", "");
        ResponseEntity<UserRest> response = getUser(token, new ParameterizedTypeReference<UserRest>(){});
        assertEquals(user.getName(), response.getBody().getName());
    }

    public <T> ResponseEntity<T> register(UserRegisterRequestModel model, Class<T> responseType){
        return testRestTemplate.postForEntity(API_REGISTER_URL, model, responseType);
    }

    public <T>ResponseEntity<T> getUser(String token, ParameterizedTypeReference<T> responseType){
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Object> entity = new HttpEntity<Object>(null, headers);
        return testRestTemplate.exchange(API_REGISTER_URL, HttpMethod.GET, entity, responseType);
    }

    public <T>ResponseEntity<T> login(UserLoginRequestModel model, ParameterizedTypeReference responseType){
        HttpEntity<UserLoginRequestModel> entity = new HttpEntity<UserLoginRequestModel>(model, new HttpHeaders());
        return testRestTemplate.exchange(API_LOGIN_URL, HttpMethod.POST, entity, responseType);
    }

}
