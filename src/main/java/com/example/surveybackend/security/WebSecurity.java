package com.example.surveybackend.security;

import com.example.surveybackend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
//@RequiredArgsConstructor
public class WebSecurity {

//    private final UserService userService;
//    private final BCryptPasswordEncoder bCryptPasswordEncoder;
//
//    public WebSecurity(UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder){
//        this.userService = userService;
//        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
//    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {

        AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationManager);
        authenticationFilter.setFilterProcessesUrl(SecurityConstants.LOGIN_URL);
        http.cors().and().csrf().disable()
//                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
//                .authorizeRequests()
//                .antMatchers(UrlMapping.AUTH + UrlMapping.SIGN_UP).permitAll()
//                .antMatchers(UrlMapping.AUTH + UrlMapping.LOGIN).permitAll()
//                .antMatchers(UrlMapping.VALIDATE_JWT).permitAll()
//                .antMatchers("/api/test/**").permitAll()
//                .anyRequest().authenticated();
//
//        http.addFilterBefore(authenticationJwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/users").permitAll()
                .antMatchers(HttpMethod.GET, "/polls/**/questions").permitAll()
                .antMatchers(HttpMethod.POST, "/polls/reply").permitAll()

                .anyRequest().authenticated();
        http.addFilter(authenticationFilter)
                .addFilter(new AuthorizationFilter(authenticationManager));
        http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


}
