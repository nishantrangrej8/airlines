package com.example.airlines.model;

import lombok.Builder;
import lombok.Value;
import org.springframework.validation.annotation.Validated;


@Value
@Builder
@Validated
public class ErrorResponse {

    String errorMessage;

    ErrorCode code;

}
