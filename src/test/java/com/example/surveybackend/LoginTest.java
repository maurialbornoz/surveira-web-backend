package com.example.surveybackend;

import com.example.surveybackend.models.request.UserLoginRequestModel;
import com.example.surveybackend.models.request.UserRegisterRequestModel;
import com.example.surveybackend.repositories.UserRepository;
import com.example.surveybackend.services.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import javax.lang.model.element.Parameterizable;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class LoginTest {

    private static final String API_LOGIN_URL = "/users/login";
    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;

    @AfterEach
    public void cleanup(){
        userRepository.deleteAll();
    }

    @Test
    public void postLogin_withoutCredentials_returnsForbidden(){
        ResponseEntity<Object> response = login(null, Object.class);
        assertEquals(response.getStatusCode(), HttpStatus.FORBIDDEN);

    }

    @Test
    public void postLogin_withIncorrectCredentials_returnsUnauthorized(){
        UserRegisterRequestModel user = TestUtil.createValidUser();
        // insert user en h2 db
        userService.createUser(user);
        UserLoginRequestModel model = new UserLoginRequestModel();
        model.setEmail("unequals@correo.com");
        model.setPassword("87654321");

        ResponseEntity<Object> response = login(model, Object.class);
        assertEquals(response.getStatusCode(), HttpStatus.FORBIDDEN);
    }

    @Test
    public void postLogin_withCorrectCredentials_returnsOk(){
        UserRegisterRequestModel user = TestUtil.createValidUser();
        // insert user en h2 db
        userService.createUser(user);
        UserLoginRequestModel model = new UserLoginRequestModel();
        model.setEmail(user.getEmail());
        model.setPassword(user.getPassword());

        ResponseEntity<Object> response = login(model, Object.class);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void postLogin_withCorrectCredentials_returnsAuthToken(){
        UserRegisterRequestModel user = TestUtil.createValidUser();
        // insert user en h2 db
        userService.createUser(user);
        UserLoginRequestModel model = new UserLoginRequestModel();
        model.setEmail(user.getEmail());
        model.setPassword(user.getPassword());
        ResponseEntity<Map<String, String>> response = login(model, new ParameterizedTypeReference<Map<String, String>>(){});
        Map<String, String> body = response.getBody();
        String token = body.get("token");
        assertTrue(token.contains("Bearer"));
    }

    public <T>ResponseEntity<T> login(UserLoginRequestModel model, Class<T> responseType){
        return testRestTemplate.postForEntity(API_LOGIN_URL, model, responseType);
    }

    public <T>ResponseEntity<T> login(UserLoginRequestModel model, ParameterizedTypeReference responseType){
        HttpEntity<UserLoginRequestModel> entity = new HttpEntity<UserLoginRequestModel>(model, new HttpHeaders());
        return testRestTemplate.exchange(API_LOGIN_URL, HttpMethod.POST, entity, responseType);
    }

}
