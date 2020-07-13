package com.instagram.auth.service.impl;

import com.instagram.auth.entity.Role;
import com.instagram.auth.entity.User;
import com.instagram.auth.exception.EmailAlreadyExistsException;
import com.instagram.auth.exception.ResourceNotFoundException;
import com.instagram.auth.exception.UsernameAlreadyExistsException;
import com.instagram.auth.message.UserEventSender;
import com.instagram.auth.repository.UserRepository;
import com.instagram.auth.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserEventSender userEventSender;

    public UserServiceImpl(PasswordEncoder passwordEncoder, UserRepository userRepository,
                           UserEventSender userEventSender) {
        this.passwordEncoder = passwordEncoder;
        this.userEventSender = userEventSender;
        this.userRepository = userRepository;
    }

    @Override
    public List<User> findAll() {
        log.info("retrieving all users");
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        log.info("retrieving user {}", username);
        return userRepository.findByUsername(username);
    }

    @Override
    public List<User> findByUsernameIn(List<String> usernames) {
        return userRepository.findByUsernameIn(usernames);
    }

    @Override
    public User registerUser(User user) {
        log.info("registering user {}", user.getUsername());
        if (userRepository.existsByUsername(user.getUsername())) {
            log.warn("username {} already exists.", user.getUsername());
            throw new UsernameAlreadyExistsException(String.format("Username %s already exists", user.getUsername()));
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            log.warn("email {} already exists.", user.getEmail());
            throw new EmailAlreadyExistsException(String.format("Email %s already exists", user.getEmail()));
        }
        user.setActive(true);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(new HashSet<>() {{
            add(Role.USER);
        }});
        User savedUser = userRepository.save(user);
        userEventSender.sendUserCreated(user);
        return savedUser;
    }

    @Override
    public User updateProfilePicture(String uri, String id) {
        log.info("update profile picture {} for user {}", uri, id);
        return userRepository.findById(id)
                .map(user -> {
                    String oldProfilePic = user.getUserProfile().getProfilePictureUrl();
                    user.getUserProfile().setProfilePictureUrl(uri);
                    User savedUser = userRepository.save(user);
                    userEventSender.sendUserUpdated(savedUser, oldProfilePic);
                    return savedUser;
                })
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User id %s not found", id)));
    }
}
