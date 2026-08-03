package com.vishwesh.jwt_authentication_series_01.service;

import com.vishwesh.jwt_authentication_series_01.entity.UserEntity;

import java.util.List;

public interface UserService {

    UserEntity registerUser(UserEntity registerDto);
    List<UserEntity> getAllUsers();

    UserEntity updateUser (Long id,UserEntity updatedUser);

    UserEntity getByEmail(String email);
}
