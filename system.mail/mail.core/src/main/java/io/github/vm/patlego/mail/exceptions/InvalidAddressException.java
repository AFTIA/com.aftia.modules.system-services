package com.aftia.modules.mail.exceptions;

public class InvalidAddressException extends Exception {

    public InvalidAddressException(String msg, Throwable t) {
        super(msg, t);
    }

    public InvalidAddressException(String msg) {
        super(msg);
    }
    
}
