package com.instagram.auth.message;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface UserEventStream {
    String OUTPUT = "instagramUserChanged";

    @Output(OUTPUT)
    MessageChannel instagramUserChanged();
}
