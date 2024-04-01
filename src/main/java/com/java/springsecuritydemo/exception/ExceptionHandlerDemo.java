package com.java.springsecuritydemo.exception;

import java.net.http.HttpHeaders;
import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerDemo extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { Exception.class })
    public ResponseEntity<Object> anyException(Exception ex, WebRequest webRequest) {
        String errorMessageDescription = ex.getLocalizedMessage();
        if (errorMessageDescription == null) {
            errorMessageDescription = ex.toString();

        }
        System.out.println("main message: "+ ex.getLocalizedMessage());
//        ErrorMessage errorMessage = new ErrorMessage(new Date(), errorMessageDescription);
        // System.out.println("erromessage : " + errorMessage);
        return new ResponseEntity<>(ex.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ErrorMessage.class)
    public ResponseEntity<Object> customHandleNotFound(ErrorMessage errorMessage, WebRequest request) {

        // ErrorMessage errors = new ErrorMessage(new Date(), errorMessage.getLocalizedMessage());

        System.out.println("message: " + errorMessage.getLocalizedMessage());
        // return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
        return new ResponseEntity(errorMessage.getLocalizedMessage(), HttpStatus.NOT_ACCEPTABLE);

    }

    @ExceptionHandler(ResourceNotFound.class)
    public ResponseEntity<Object> resouceNotFound(ResourceNotFound resourceNotFound, WebRequest request) {

        ResourceNotFound res = new ResourceNotFound(resourceNotFound.getLocalizedMessage());

        System.out.println("ResourceNotFound: " + res);
        return new ResponseEntity<>(resourceNotFound.getLocalizedMessage(),HttpStatus.NOT_FOUND);

    }
//    @Override
//    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
//        BindingResult result = ex.getBindingResult();
//        String message = result.getFieldErrors().stream()
//                .map(DefaultMessageSourceResolvable::getDefaultMessage)
//                .findFirst()
//                .orElse("Validation error");
//
//        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
//    }

}
