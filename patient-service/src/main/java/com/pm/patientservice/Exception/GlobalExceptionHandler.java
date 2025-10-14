package com.pm.patientservice.Exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler

{
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>>handleValidationException(MethodArgumentNotValidException ex){
        Map<String,String> errors=new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(fieldError -> errors.put(fieldError.getField(), fieldError.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String,String>>handleEmailAlreadyExistsException(EmailAlreadyExistsException ex){
        log.warn("email address already exists{}",ex.getMessage());
        Map<String,String> errors=new HashMap<>();
        errors.put("message","Email address already exists");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(PatientNotFoundException.class)
    public ResponseEntity<Map<String,String>>handlePatientNotFoundException(PatientNotFoundException ex){
        log.warn("Patient not found for the provided ID{}",ex.getMessage());
        Map<String,String> errors=new HashMap<>();
        errors.put("message","Patient not found");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}
