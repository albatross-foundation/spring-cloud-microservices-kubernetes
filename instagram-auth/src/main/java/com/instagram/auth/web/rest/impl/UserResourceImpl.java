package com.instagram.auth.web.rest.impl;

import com.instagram.auth.config.JwtTokenProvider;
import com.instagram.auth.domain.*;
import com.instagram.auth.entity.InstagramUserDetails;
import com.instagram.auth.entity.Profile;
import com.instagram.auth.entity.User;
import com.instagram.auth.exception.BadRequestException;
import com.instagram.auth.exception.EmailAlreadyExistsException;
import com.instagram.auth.exception.ResourceNotFoundException;
import com.instagram.auth.exception.UsernameAlreadyExistsException;
import com.instagram.auth.service.UserService;
import com.instagram.auth.web.rest.UserResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class UserResourceImpl implements UserResource {

    private final UserService userService;

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider tokenProvider;

    public UserResourceImpl(UserService userService, AuthenticationManager authenticationManager,
                            JwtTokenProvider tokenProvider) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public ResponseEntity<?> authenticateUser(@Valid LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }

    @Override
    public ResponseEntity<?> createUser(@Valid SignUpRequest signUpRequest) {
        User user = User.builder()
                .email(signUpRequest.getEmail())
                .username(signUpRequest.getUsername())
                .password(signUpRequest.getPassword())
                .userProfile(Profile.builder()
                        .displayName(signUpRequest.getName())
                        .build())
                .build();
        try {
            userService.registerUser(user);
        } catch (UsernameAlreadyExistsException | EmailAlreadyExistsException e) {
            throw new BadRequestException(e.getMessage());
        }

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/{username}")
                .buildAndExpand(user.getUsername()).toUri();
        return ResponseEntity
                .created(location)
                .body(new ApiResponse(true, "User registered successfully"));
    }

    @Override
    public ResponseEntity<?> updateProfilePicture(String profilePicture, InstagramUserDetails userDetails) {
        userService.updateProfilePicture(profilePicture, userDetails.getId());
        return ResponseEntity
                .ok()
                .body(new ApiResponse(true, "Profile picture updated successfully"));
    }

    @Override
    public ResponseEntity<?> findUser(String username) {
        return userService.findByUsername(username)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(username));
    }

    @Override
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @Override
    public UserSummary getCurrentUser(InstagramUserDetails userDetails) {
        return UserSummary.builder()
                .id(userDetails.getId())
                .username(userDetails.getUsername())
                .name(userDetails.getUserProfile().getDisplayName())
                .profilePicture(userDetails.getUserProfile().getProfilePictureUrl())
                .build();
    }

    @Override
    public ResponseEntity<?> getUserSummary(String username) {
        return userService.findByUsername(username)
                .map(user -> ResponseEntity.ok(convertTo(user)))
                .orElseThrow(() -> new ResourceNotFoundException(username));
    }

    @Override
    public ResponseEntity<?> getUserSummaries(List<String> usernames) {
        List<UserSummary> userSummaries = userService.findByUsernameIn(usernames)
                .stream()
                .map(this::convertTo)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userSummaries);
    }

    private UserSummary convertTo(User user) {
        return UserSummary
                .builder()
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getUserProfile().getDisplayName())
                .profilePicture(user.getUserProfile().getProfilePictureUrl())
                .build();
    }
}
