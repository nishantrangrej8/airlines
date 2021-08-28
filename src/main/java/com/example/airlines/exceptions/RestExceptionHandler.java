package com.example.airlines.exceptions;

import com.example.airlines.model.ErrorCode;
import com.example.airlines.model.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Validated
@ControllerAdvice
public class RestExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EmptyListException.class)
    public ErrorResponse handleEmptyListError(final EmptyListException e) {
        LOG.warn("Customer data list is empty", e);
        return ErrorResponse.builder()
                .errorMessage("Customer data list is empty. Please run /flyers/load/{noOfCustomers}")
                .code(ErrorCode.NOT_FOUND)
                .build();
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(CustomerNotFoundException.class)
    public ErrorResponse handleNoFrequentFlyersFoundError(final CustomerNotFoundException e) {
        LOG.warn("No Frequent flyers found > 100K miles. ", e);
        return ErrorResponse.builder()
                .errorMessage("No Frequent flyers found.")
                .code(ErrorCode.NOT_FOUND)
                .build();
    }
}
