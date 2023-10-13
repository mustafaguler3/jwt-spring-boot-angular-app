package com.example.demo.service;

import com.example.demo.domain.User;
import com.example.demo.exceptions.EmailExistException;
import com.example.demo.exceptions.UsernameExistException;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

public interface UserService {

    User register(String firstName,String lastName,String username,String email) throws EmailExistException, UsernameExistException, MessagingException;
    List<User> getUsers();
    User findUserByUsername(String username);
    User findUserByEmail(String email);

    User addNewUser(String firstName,String lastName,String username,String email,String role,boolean isLocked,boolean isActive,MultipartFile profileImage) throws EmailExistException, UsernameExistException, IOException;
    User updateUser(String currentUsername,String newFirstName,String newLastName,String newUsername,String newEmail,String role,boolean isLocked,boolean isActive,MultipartFile profileImage) throws IOException, EmailExistException, UsernameExistException;
    void deleteUser(String username) throws IOException;
    void resetPassword(String email) throws MessagingException;
    User updateProfileImage(String username, MultipartFile profileImage) throws EmailExistException, UsernameExistException, IOException;
}

















