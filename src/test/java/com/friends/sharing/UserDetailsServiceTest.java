package com.friends.sharing;

import com.friends.sharing.configuration.security.UserAdapter;
import com.friends.sharing.dto.request.RegistrationRequest;
import com.friends.sharing.model.User;
import com.friends.sharing.repository.UserRepository;
import com.friends.sharing.service.UserDetailsServiceImp;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceTest {
    @Mock
    UserRepository userRepository;
    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserDetailsServiceImp userDetailsService;

    @Test
    @DisplayName("Test for registration")
    void testRegistration() {
        ResponseEntity<String> expect = new ResponseEntity<>("Successfully registered, your email is your username",
                HttpStatus.CREATED);
        RegistrationRequest registrationRequest = new RegistrationRequest(
                "name",
                "email@gmail.com",
                "1234",
                "USER"
        );

        when(userRepository.findUserByEmail(registrationRequest.getEmail()))
                .thenReturn(Optional.empty());

        assertThat(userDetailsService.register(registrationRequest)).isEqualTo(expect);
    }

    @Test
    @DisplayName("Test for registration, already registered")
    void testRegistration_AlreadyRegistered() {
        ResponseEntity<String> expect = new ResponseEntity<>("Such a user already exists!",
                HttpStatus.BAD_REQUEST);
        RegistrationRequest registrationRequest = new RegistrationRequest(
                "name",
                "email@gmail.com",
                "1234",
                "USER"
        );

        when(userRepository.findUserByEmail(registrationRequest.getEmail()))
                .thenReturn(Optional.of(new User()));

        assertThat(userDetailsService.register(registrationRequest)).isEqualTo(expect);
    }

    @Test
    @DisplayName("Test for registration, wrong authority")
    void testRegistration_WrongRole() {
        ResponseEntity<String> expect = new ResponseEntity<>("Wrong authority provided",
                HttpStatus.BAD_REQUEST);
        RegistrationRequest registrationRequest = new RegistrationRequest(
                "name",
                "email@gmail.com",
                "1234",
                "megauser"
        );

        when(userRepository.findUserByEmail(registrationRequest.getEmail()))
                .thenReturn(Optional.empty());

        assertThat(userDetailsService.register(registrationRequest)).isEqualTo(expect);
    }

    @Test
    @DisplayName("Test for authentication")
    void testAuthentication() {
        String email = "email@gmail.com";
        User user = new User();
        UserAdapter expect = new UserAdapter(user);

        when(userRepository.findUserByEmail(email))
                .thenReturn(Optional.of(user));

        assertThat(userDetailsService.loadUserByUsername(email)).isEqualTo(expect);
    }

    @Test
    @DisplayName("Test for authentication, user not found")
    void testAuthentication_NoUser() {
        String email = "email@gmail.com";

        when(userRepository.findUserByEmail(email))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(email))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("Not found!");
    }
}
