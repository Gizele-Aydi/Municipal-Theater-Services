package org.example.municipaltheater.models;

public class APIResponse<E> {

    private int statusCode;
    private String message;
    private E data;

    public APIResponse(int statusCode, String message, E data) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }

    public APIResponse(int statusCode, String message) {
        this(statusCode, message, null);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public E getData() {
        return data;
    }

    public void setData(E data) {
        this.data = data;
    }
}
