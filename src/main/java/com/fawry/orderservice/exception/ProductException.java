package com.fawry.orderservice.exception;

import java.util.List;

public class ProductException extends RuntimeException {
    private List<String> errors;

    public ProductException(List<String> errors) {
        super("Product errors occurred");
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}
