package com.example.airlines.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum ErrorCode {
    BAD_REQUEST(4000),

    INVALID_INPUT(4001),

    UNAUTHENTICATED(4010),

    UNAUTHORIZED(4030),

    NOT_FOUND(4040),

    METHOD_NOT_ALLOWED(4050),

    UNSUPPORTED_MEDIA_TYPE(4150),

    INTERNAL_SERVER_ERROR(5000);

    private final int code;

    ErrorCode(final int code) {
        this.code = code;
    }

    @JsonValue
    public int getCode() {
        return code;
    }

}