package com.guidodelbo.usercrud.exception;

public class UserServiceException extends RuntimeException{

    private static final long serialVersionUID = -3751884750124750328L;

    public UserServiceException(String message) {
        super(message);
    }
}
