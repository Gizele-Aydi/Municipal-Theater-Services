package org.example.municipaltheater.utils;

public class DefinedExceptions {

    public static class ONotFoundException extends RuntimeException {
        public ONotFoundException(String message) {
            super(message);
        }
    }
    public static class OAlreadyExistsException extends RuntimeException {
        public OAlreadyExistsException(String message) {
            super(message);
        }
    }
    public static class OServiceException extends RuntimeException {
        public OServiceException(String message) {
            super(message);
        }
    }
    public static class OConstrainViolationException extends RuntimeException {
        public OConstrainViolationException(String message) { super(message); }
    }

}
