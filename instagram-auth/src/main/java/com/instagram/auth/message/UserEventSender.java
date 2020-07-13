package com.instagram.auth.message;

import com.instagram.auth.entity.User;

public interface UserEventSender {
    public void sendUserCreated(User user);
    public void sendUserUpdated(User user);
    public void sendUserUpdated(User user, String oldPicUrl);
}
