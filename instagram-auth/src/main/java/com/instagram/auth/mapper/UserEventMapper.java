package com.instagram.auth.mapper;

import com.instagram.auth.domain.UserEventResponse;
import com.instagram.auth.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserEventMapper extends EntityMapper<User, UserEventResponse> {
}
