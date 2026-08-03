package com.vishwesh.jwt_authentication_series_01.mapper;

import com.vishwesh.jwt_authentication_series_01.dto.request.UserRequest;
import com.vishwesh.jwt_authentication_series_01.dto.response.UserResponse;
import com.vishwesh.jwt_authentication_series_01.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserEntity toEntity(UserRequest request) {
        UserEntity user = new UserEntity();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        return user;
    }

    public UserResponse toResponse(UserEntity user) {

        UserResponse response = new UserResponse();

        response.setId(user.getUserId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());

        return response;
    }
}

/**
 * Why @Component?
 * @Component
 *
 * Spring creates an object (bean) for this mapper.
 */
