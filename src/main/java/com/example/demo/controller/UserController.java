package com.example.demo.controller;

import com.example.demo.config.UserPricipal;
import com.example.demo.domain.User;
import com.example.demo.exceptions.EmailExistException;
import com.example.demo.exceptions.ExceptionHandling;
import com.example.demo.exceptions.UsernameExistException;
import com.example.demo.service.UserService;
import com.example.demo.utility.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.constants.SecurityConstant.JWT_TOKEN_HEADER;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(value = "/user")
public class UserController extends ExceptionHandling {

    private UserService userService;
    private AuthenticationManager authenticationManager;
    private TokenProvider tokenProvider;

    @Autowired
    public UserController(UserService userService, AuthenticationManager authenticationManager, TokenProvider tokenProvider) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody User user){
        authenticate(user.getUsername(),user.getPassword());
        User loginUser = userService.findUserByUsername(user.getUsername());
        UserPricipal userPricipal = new UserPricipal(loginUser);
        HttpHeaders jwtHeader = getJwtHeader(userPricipal);
        return new ResponseEntity<>(loginUser,jwtHeader,OK);
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) throws EmailExistException, UsernameExistException {
        User newUser = userService.register(user.getFirstName(),user.getLastName(),user.getUsername(),user.getEmail());

        return new ResponseEntity<>(newUser, OK);
    }

    private HttpHeaders getJwtHeader(UserPricipal userPricipal){
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWT_TOKEN_HEADER,tokenProvider.generateToken(userPricipal));
        return headers;
    }

    private void authenticate(String username,String password){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username,password));
    }
}




















