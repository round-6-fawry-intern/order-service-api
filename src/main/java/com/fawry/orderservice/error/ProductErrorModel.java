package com.fawry.orderservice.error;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductErrorModel {
    private boolean success;
    private String message;
    private List<String> details;
    private LocalDateTime dateTime;

    private int statusCode;

    public ProductErrorModel(List<String> errors) {
        this.details = errors;
    }
}
