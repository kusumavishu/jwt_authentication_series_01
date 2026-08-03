//"How do we fetch this user from PostgreSQL using their email?"
package com.vishwesh.jwt_authentication_series_01.security;

import com.vishwesh.jwt_authentication_series_01.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(
            UserRepository userRepository
    ){
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("\n--------------------------------------------------");
        System.out.println(">>> [DEBUG] CustomUserDetailsService: Executing loadUserByUsername()");
        System.out.println(">>> [DEBUG] Searching database for email: " + email);

        return userRepository.findByEmail(email)
                .map(user -> {
                    System.out.println(">>> [DEBUG] User FOUND in DB!");
                    System.out.println(">>> [DEBUG] User ID: " + user.getUserId());
                    System.out.println(">>> [DEBUG] Hashed Password from DB: " + user.getPassword());
                    System.out.println(">>> [DEBUG] Wrapping UserEntity into CustomUserDetails...");

                    CustomUserDetails userDetails = new CustomUserDetails(user);
                    System.out.println(">>> [DEBUG] CustomUserDetails successfully created.");
                    System.out.println("--------------------------------------------------\n");
                    return userDetails;
                })
                .orElseThrow(() -> {
                    System.err.println(">>> [ERROR] User NOT FOUND in DB for email: " + email);
                    System.err.println("--------------------------------------------------\n");
                    return new UsernameNotFoundException("User not found with email: " + email);
                });
    }
}


//                .map(CustomUserDetails::new)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));