package com.instagram.auth.message.impl;

import com.instagram.auth.domain.UserEventResponse;
import com.instagram.auth.entity.User;
import com.instagram.auth.message.UserEventSender;
import com.instagram.auth.message.UserEventStream;
import com.instagram.auth.message.UserEventType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserEventSenderImpl implements UserEventSender {

    private final UserEventStream channels;

    public UserEventSenderImpl(UserEventStream channels) {
        this.channels = channels;
    }

    @Override
    public void sendUserCreated(User user) {
        log.info("sending user created event for user {}", user.getUsername());
        sendUserChangedEvent(toDTO(user, UserEventType.CREATED));
    }

    @Override
    public void sendUserUpdated(User user) {
        log.info("sending user updated event for user {}", user.getUsername());
        sendUserChangedEvent(toDTO(user, UserEventType.UPDATED));
    }

    @Override
    public void sendUserUpdated(User user, String oldPicUrl) {
        log.info("sending user updated (profile pic changed) event for user {}", user.getUsername());
        UserEventResponse userEventResponse = toDTO(user, UserEventType.CREATED);
        userEventResponse.setOldProfilePicUrl(oldPicUrl);
        sendUserChangedEvent(userEventResponse);
    }

    private void sendUserChangedEvent(UserEventResponse payload) {

        Message<UserEventResponse> message =
                MessageBuilder
                        .withPayload(payload)
                        .setHeader(KafkaHeaders.MESSAGE_KEY, payload.getId())
                        .build();
        channels.instagramUserChanged().send(message);
        log.info("user event {} sent to topic {} for user {}",
                message.getPayload().getEventType().name(),
                channels.OUTPUT,
                message.getPayload().getUsername());
    }

    private UserEventResponse toDTO(User user, UserEventType eventType) {
        return UserEventResponse.builder()
                .id(user.getId())
                .eventType(eventType)
                .username(user.getUsername())
                .email(user.getEmail())
                .displayName(user.getUserProfile().getDisplayName())
                .profilePictureUrl(user.getUserProfile().getProfilePictureUrl())
                .build();
    }
}
