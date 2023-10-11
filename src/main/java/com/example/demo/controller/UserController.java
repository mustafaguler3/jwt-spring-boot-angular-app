package com.example.demo.controller;

import com.example.demo.domain.User;
import com.example.demo.exceptions.EmailExistException;
import com.example.demo.exceptions.ExceptionHandling;
import com.example.demo.exceptions.UsernameExistException;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/user")
public class UserController extends ExceptionHandling {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) throws EmailExistException, UsernameExistException {
        User newUser = userService.register(user.getFirstName(),user.getLastName(),user.getUsername(),user.getEmail());

        return new ResponseEntity<>(newUser, HttpStatus.OK);
    }
}




















