package com.personel.auth.server.exceptions;

public class ObjectNotValidException extends RuntimeException{
    public ObjectNotValidException(String message){
        super(message);
    }
}
