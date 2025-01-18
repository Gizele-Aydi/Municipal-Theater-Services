package org.example.municipaltheater.utils;

import org.example.municipaltheater.models.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ExceptionsHandler {

    @ExceptionHandler(DefinedExceptions.ONotFoundException.class)
    public ResponseEntity<APIResponse<Object>> handleNotFound(DefinedExceptions.ONotFoundException ex) {
        return new ResponseEntity<>(new APIResponse<>(HttpStatus.NOT_FOUND.value(), ex.getMessage(), null), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DefinedExceptions.OAlreadyExistsException.class)
    public ResponseEntity<APIResponse<Object>> handleAlreadyExists(DefinedExceptions.OAlreadyExistsException ex) {
        return new ResponseEntity<>(new APIResponse<>(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DefinedExceptions.OServiceException.class)
    public ResponseEntity<APIResponse<Object>> handleServiceException(DefinedExceptions.OServiceException ex) {
        return new ResponseEntity<>(new APIResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<APIResponse<Object>> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String propertyPath = violation.getPropertyPath().toString();
            String errorMessage = violation.getMessage();
            errors.put(propertyPath, errorMessage);
        });
        return new ResponseEntity<>(new APIResponse<>(HttpStatus.BAD_REQUEST.value(), "Validation failed", errors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIResponse<Object>> handleGenericException(Exception ex) {
        return new ResponseEntity<>(new APIResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred: " + ex.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIResponse<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(new APIResponse<>(HttpStatus.BAD_REQUEST.value(), "Validation failed", errors), HttpStatus.BAD_REQUEST);
    }

}
