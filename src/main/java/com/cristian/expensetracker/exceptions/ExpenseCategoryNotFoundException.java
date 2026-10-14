package com.cristian.expensetracker.exceptions;

public class ExpenseCategoryNotFoundException extends RuntimeException {
    public ExpenseCategoryNotFoundException(String message) {
        super(message);
    }
}
