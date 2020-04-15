package com.backend.tasks.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiError {

    private String field;
    private String value;
    private String message;

    public ApiError(String message) {
        this.message = message;
    }
}
