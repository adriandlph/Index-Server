package com.adlph.internal.managment.index.server.exception;

import lombok.Getter;

@Getter
public class ServerErrorException extends Exception {

    private final int code;

    public ServerErrorException(int code, String message) {
        super(message);
        this.code = code;
    }

    public ServerErrorException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
}
