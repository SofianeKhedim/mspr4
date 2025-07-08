package com.example.clientapi.exception;

/**
 * Exception levée lorsqu'un client n'est pas trouvé.
 */
public class ClientNotFoundException extends RuntimeException {

    public ClientNotFoundException(String message) {
        super(message);
    }

    public ClientNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}