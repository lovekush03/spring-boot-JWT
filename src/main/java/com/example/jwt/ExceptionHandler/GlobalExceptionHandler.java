package com.example.jwt.ExceptionHandler;

import com.example.jwt.CustomExceptions.UserAlreadyExistsException;
import com.example.jwt.CustomExceptions.UserNotFoundException;
import com.example.jwt.Dto.ErrorResponseDTO;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserAlreadyExistsException(UserAlreadyExistsException exp){
        return new ResponseEntity<>(
            ErrorResponseDTO.builder()
                    .errorMessage(exp.getMessage())
                    .errorOccurred(LocalDateTime.now())
                    .statusCode(HttpStatus.BAD_REQUEST)
                    .build(),
            HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserNotFoundException(UserNotFoundException exp){
        return new ResponseEntity<>(
                ErrorResponseDTO.builder()
                        .errorMessage(exp.getMessage())
                        .errorOccurred(LocalDateTime.now())
                        .statusCode(HttpStatus.BAD_REQUEST)
                        .build(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponseDTO> handleExpiredJwtException(ExpiredJwtException exp){
        return new ResponseEntity<>(
                ErrorResponseDTO.builder()
                        .errorMessage(exp.getMessage())
                        .errorOccurred(LocalDateTime.now())
                        .statusCode(HttpStatus.UNAUTHORIZED)
                        .build(),
                HttpStatus.UNAUTHORIZED
        );
    }
}
