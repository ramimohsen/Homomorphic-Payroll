package org.homomorphicpayroll.exception.custom;

public class EmployeeAlreadyExistException extends Exception {
    public EmployeeAlreadyExistException(String message) {
        super(message);
    }

}
