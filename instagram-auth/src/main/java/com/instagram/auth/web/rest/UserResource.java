package com.instagram.auth.web.rest;

import com.instagram.auth.domain.LoginRequest;
import com.instagram.auth.domain.SignUpRequest;
import com.instagram.auth.domain.UserSummary;
import com.instagram.auth.entity.InstagramUserDetails;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "User Resource")
//@RequestMapping("/user")
@Validated
public interface UserResource {
    @ApiOperation(value = "Sign in")
    @PostMapping("/signin")
    ResponseEntity<?> authenticateUser(
            @ApiParam(value = "Request login body", required = true) @Valid @RequestBody LoginRequest loginRequest
    );

    @ApiOperation(value = "Create user")
    @PostMapping("/create")
    ResponseEntity<?> createUser(
            @ApiParam(value = "Request create body", required = true) @Valid @RequestBody SignUpRequest signUpRequest
    );

    @ApiOperation(value = "Update profile picture")
    @PutMapping("/me/picture")
    @PreAuthorize("hasRole('USER')")
    ResponseEntity <?> updateProfilePicture(
            @ApiParam(value = "Profile picture url", required = true) @RequestBody String profilePicture,
            @ApiParam(value = "User details", required = true) @AuthenticationPrincipal InstagramUserDetails userDetails
    );

    @ApiOperation(value = "Find user")
    @GetMapping(value = "/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> findUser(
            @ApiParam(value = "User name", required = true) @PathVariable("username") String username
    );

    @ApiOperation(value = "Find all user")
    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> findAll();

    @ApiOperation(value = "Get current user")
    @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.OK)
    UserSummary getCurrentUser(
            @ApiParam(value = "User details", required = true) @AuthenticationPrincipal InstagramUserDetails userDetails
    );

    @ApiOperation(value = "Get user summary")
    @GetMapping(value = "/summary/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getUserSummary(
            @ApiParam(value = "User name", required = true) @PathVariable("username") String username
    );

    @ApiOperation(value = "Get user summary in")
    @PostMapping(value = "/summary/in", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getUserSummaries(
            @ApiParam(value = "User names", required = true) @RequestBody List<String> usernames
    );
}
