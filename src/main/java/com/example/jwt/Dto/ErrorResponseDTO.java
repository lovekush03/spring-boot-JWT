package com.example.jwt.Dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ErrorResponseDTO {
    private String errorMessage;
    private LocalDateTime errorOccurred;
    private HttpStatus statusCode;
}
