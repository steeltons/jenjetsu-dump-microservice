package org.jenjetsu.com.crm.exception;

public class PasswordEqualException extends Exception{

    public PasswordEqualException() {
        super();
    }

    public PasswordEqualException(String message) {
        super(message);
    }
}
