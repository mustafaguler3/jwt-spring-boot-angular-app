package com.example.demo.service.Impl;

import com.example.demo.config.UserPricipal;
import com.example.demo.domain.User;
import com.example.demo.repositories.UserRepository;
import com.example.demo.service.UserService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;


@Service
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {

    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username);

        if (user == null){
            LOGGER.error("User not found by username "+username);
            throw new UsernameNotFoundException("User not found by username " + username);
        }else {
            user.setLastLoginDateDisplay(user.getLastLoginDate());
            user.setLastLoginDate(new Date());
            userRepository.save(user);
            UserPricipal userPricipal = new UserPricipal(user);

            LOGGER.info("Returning found user by username "+ username);
        }
        return null;
    }

}





















