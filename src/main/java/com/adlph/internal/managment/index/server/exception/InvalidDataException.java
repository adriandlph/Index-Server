package com.adlph.internal.managment.index.server.exception;

import lombok.Getter;

@Getter
public class InvalidDataException extends Exception {
    public InvalidDataException(String message) {
        super(message);
    }
}
