package com.friends.sharing.controller.advice;

import com.friends.sharing.exception.ConflictException;
import com.friends.sharing.exception.ItemException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.Optional;

@RestControllerAdvice
public class FriendsSharingAdvice {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleArgumentNotValid(MethodArgumentNotValidException exception) {
        Optional<String> message = Optional.ofNullable(
                exception.getBindingResult().getFieldErrors().get(0).getDefaultMessage());
        return Map.of("error", message.orElse(exception.getMessage()));
    }

    @ExceptionHandler(ItemException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleCreateOrderException(ItemException exception) {
        return Map.of("error", exception.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleConflictException(ConflictException exception) {
        return Map.of("error", exception.getMessage());
    }
}
