package com.vishwesh.jwt_authentication_series_01.service.impl;

import com.vishwesh.jwt_authentication_series_01.entity.UserEntity;
import com.vishwesh.jwt_authentication_series_01.exception.ApiException;
import com.vishwesh.jwt_authentication_series_01.repository.UserRepository;
import com.vishwesh.jwt_authentication_series_01.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service // <--- Make sure this annotation is present!
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public UserServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public UserEntity registerUser(UserEntity user) {

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    @Override
    public UserEntity updateUser(Long id, UserEntity updatedUser){
        UserEntity existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,"USER NOT FOUND"));

        existingUser.setName(updatedUser.getName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPassword(updatedUser.getPassword());

        return userRepository.save(existingUser);
    }

    @Override
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public UserEntity getByEmail(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,"USER NOT FOUND"));
    }

}

/**
 * Instead of
 * @Autowired
 * private UserRepository repository;
 *
 *Use
 * private final UserRepository repository;
 *
 * public UserService(UserRepository repository) {
 *     this.repository = repository;
 * }
 */
