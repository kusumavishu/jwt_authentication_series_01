package com.vishwesh.jwt_authentication_series_01.controller;

import com.vishwesh.jwt_authentication_series_01.dto.request.LoginRequest;
import com.vishwesh.jwt_authentication_series_01.dto.request.UserRequest;
import com.vishwesh.jwt_authentication_series_01.dto.response.UserResponse;
import com.vishwesh.jwt_authentication_series_01.entity.UserEntity;
import com.vishwesh.jwt_authentication_series_01.mapper.UserMapper;
import com.vishwesh.jwt_authentication_series_01.service.UserService;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    // Constructor Injection
    public UserController(
            UserService userService,
            UserMapper userMapper
    ) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    // Create User
    @PostMapping
    public UserResponse createUser(@Valid @RequestBody UserRequest request) {
          System.out.println("Controller reached");

//        UserEntity user = new UserEntity();
//        user.setName(request.getName());
//        user.setEmail(request.getEmail());
//        user.setPassword(request.getPassword());

        UserEntity user = userMapper.toEntity(request);
        UserEntity savedUser = userService.registerUser(user);

        System.out.println("User saved");

        return userMapper.toResponse(savedUser);

//        return new UserResponse(
//                savedUser.getUserId(),
//                savedUser.getName(),
//                savedUser.getEmail()
//        );
    }

    // Get All Users
    @GetMapping
    public List<UserEntity> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/test")
    public String checkTest(){
        return "CHECKING DONE";
    }

    @PutMapping("/{id}")
    public UserEntity updateUser(
            @PathVariable Long id,
            @RequestBody UserEntity updatedUser
    ){
        return userService.updateUser(id, updatedUser);
    }

    @GetMapping("/{email}")
    public UserResponse getUser(@PathVariable String email) {
        UserEntity savedUser = userService.getByEmail(email);
        return userMapper.toResponse(savedUser);
    }
}

/**
 * Use the Mapper in Service
 *
 * Instead of:
 *
 * UserEntity user = new UserEntity();
 *
 * user.setName(request.getName());
 * user.setEmail(request.getEmail());
 * user.setPassword(request.getPassword());
 *
 * Write:
 *
 * UserEntity user = userMapper.toEntity(request);
 */
