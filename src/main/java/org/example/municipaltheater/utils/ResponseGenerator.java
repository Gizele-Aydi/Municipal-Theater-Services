package org.example.municipaltheater.utils;

import org.example.municipaltheater.models.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseGenerator {

    public static <T> ResponseEntity<APIResponse<T>> Response(HttpStatus status, String message, T data) {
        return ResponseEntity.status(status).body(new APIResponse<>(status.value(), message, data));
    }
}
