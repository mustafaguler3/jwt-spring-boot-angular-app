package com.example.demo.service;

import com.example.demo.domain.User;
import com.example.demo.exceptions.EmailExistException;
import com.example.demo.exceptions.UsernameExistException;

import java.util.List;

public interface UserService {

    User register(String firstName,String lastName,String username,String email) throws EmailExistException, UsernameExistException;
    List<User> getUsers();
    User findUserByUsername(String username);
    User findUserByEmail(String email);
}

















