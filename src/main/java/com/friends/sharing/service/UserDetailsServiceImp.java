package com.friends.sharing.service;

import com.friends.sharing.configuration.security.Authorities;
import com.friends.sharing.configuration.security.UserAdapter;
import com.friends.sharing.repository.UserRepository;
import com.friends.sharing.dto.request.RegistrationRequest;
import com.friends.sharing.model.User;

import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@AllArgsConstructor
public class UserDetailsServiceImp implements UserDetailsService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public ResponseEntity<String> register(RegistrationRequest registrationRequest) {
        if (userRepository.findUserByEmail(registrationRequest.getEmail()).isPresent()) {
            return new ResponseEntity<>("Such a user already exists!",
                    HttpStatus.BAD_REQUEST);
        }

        if (Arrays.stream(Authorities.values()).noneMatch(x -> x.name().equals(registrationRequest.getAuthority().toUpperCase()))){
            return new ResponseEntity<>("Wrong authority provided",
                    HttpStatus.BAD_REQUEST);
        }
        Authorities authority = Authorities.valueOf((registrationRequest.getAuthority().toUpperCase()));

        User user = User.builder()
                .name(registrationRequest.getName())
                .email(registrationRequest.getEmail())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .authority(authority)
                .build();
        userRepository.save(user);

        return new ResponseEntity<>("Successfully registered, your email is your username", HttpStatus.CREATED);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Not found!"));

        return new UserAdapter(user);
    }
}
