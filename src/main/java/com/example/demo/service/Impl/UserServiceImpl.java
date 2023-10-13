package com.example.demo.service.Impl;

import com.example.demo.config.UserPricipal;
import com.example.demo.domain.User;
import com.example.demo.exceptions.EmailExistException;
import com.example.demo.exceptions.UsernameExistException;
import com.example.demo.repositories.UserRepository;
import com.example.demo.service.LoginAttemptService;
import com.example.demo.service.UserService;
import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.example.demo.constants.Role.ROLE_USER;
import static org.apache.logging.log4j.util.Strings.EMPTY;


@Service
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {

    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private LoginAttemptService loginAttemptService
            ;
    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, LoginAttemptService loginAttemptService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.loginAttemptService = loginAttemptService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username);

        if (user == null){
            LOGGER.error("User not found by username "+username);
            throw new UsernameNotFoundException("User not found by username " + username);
        }else {
            try {
                validateLoginAttempt(user);
                user.setLastLoginDateDisplay(user.getLastLoginDate());
                user.setLastLoginDate(new Date());
                userRepository.save(user);
                UserPricipal userPricipal = new UserPricipal(user);

                LOGGER.info("Returning found user by username "+ username);
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    private void validateLoginAttempt(User user) throws ExecutionException {
        if (user.isLocked() != true){
            if (loginAttemptService.hasExceededMaxAttempts(user.getUsername())){
                user.setLocked(false);
            }else {
                user.setLocked(true);
            }
        }else {
            loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
        }
    }

    @Override
    public User register(String firstName, String lastName, String username, String email) throws EmailExistException, UsernameExistException {
        validateNewUsernameAndEmail(EMPTY,username,email);
        User user = new User();
        user.setUserId(generateUserId());
        String password = generatePassword();
        String encodedPassword = encodePassword(password);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setEmail(email);
        user.setJoinDate(new Date());
        user.setPassword(encodedPassword);
        user.setActive(true);
        user.setLocked(false);
        user.setRole(ROLE_USER.name());
        user.setAuthorities(ROLE_USER.getAuthorities());
        user.setImageUrl(getTemporaryImageUrl());
        userRepository.save(user);
        LOGGER.info("New user password : "+password);
        return user;
    }

    private String getTemporaryImageUrl(){
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/image/profile/temp").toUriString();
    }
    private String encodePassword(String password){
        return passwordEncoder.encode(password);
    }
    private String generatePassword(){
        return RandomStringUtils.randomAlphabetic(10);
    }
    private String generateUserId(){
        return RandomStringUtils.randomAlphabetic(10);
    }

    private User validateNewUsernameAndEmail(String currentUsername,String newUsername,String newEmail) throws UsernameExistException, EmailExistException {
        if (StringUtils.isNotBlank(currentUsername)){
            User currentUser = findUserByUsername(currentUsername);
            if (currentUser == null){
                throw new UsernameNotFoundException("No user found by username "+currentUsername);
            }

            User userByUsername = findUserByUsername(newUsername);
            if (userByUsername != null && !currentUser.getId().equals(userByUsername.getId())){
                throw new UsernameExistException("Username already exists");
            }

            User userByEmail = findUserByUsername(newEmail);
            if (userByEmail != null && !currentUser.getId().equals(userByEmail.getId())){
                throw new EmailExistException("Email already exists");
            }

            return currentUser;
        }else {
            User userByUsername = findUserByUsername(newUsername);
            if (userByUsername != null){
                throw new UsernameExistException("Username already exists");
            }

            User userByEmail = findUserByUsername(newEmail);
            if (userByEmail != null){
                throw new EmailExistException("Email already exists");
            }

            return null;
        }
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }
}





















