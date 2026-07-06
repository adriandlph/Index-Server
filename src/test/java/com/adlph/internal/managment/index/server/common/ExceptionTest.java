package com.adlph.internal.managment.index.server.common;

import com.adlph.internal.managment.index.server.exception.InvalidDataException;
import com.adlph.internal.managment.index.server.exception.ServerErrorException;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionTest {

    @Test
    void invalidDataException() {
        var ex = new InvalidDataException("Test error");
        assertEquals("Test error", ex.getMessage());
    }

    @Test
    void serverErrorExceptionWithCode() {
        var ex = new ServerErrorException(42, "Server error");
        assertEquals(42, ex.getCode());
        assertEquals("Server error", ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    void serverErrorExceptionWithCause() {
        var cause = new RuntimeException("Root cause");
        var ex = new ServerErrorException(-1, "Wrapped", cause);
        assertEquals(-1, ex.getCode());
        assertEquals("Wrapped", ex.getMessage());
        assertSame(cause, ex.getCause());
    }

    @Test
    void invalidDataNullMessage() {
        var ex = new InvalidDataException(null);
        assertNull(ex.getMessage());
    }

    @Test
    void invalidDataEmptyMessage() {
        var ex = new InvalidDataException("");
        assertEquals("", ex.getMessage());
    }

    @Test
    void invalidDataSpecialCharacters() {
        var ex = new InvalidDataException("tab\tnewline\nunicode✓");
        assertEquals("tab\tnewline\nunicode✓", ex.getMessage());
    }

    @Test
    void invalidDataVeryLongMessage() {
        String longMsg = "x".repeat(100000);
        var ex = new InvalidDataException(longMsg);
        assertEquals(longMsg, ex.getMessage());
    }

    @Test
    void serverErrorCodeZero() {
        var ex = new ServerErrorException(0, "zero");
        assertEquals(0, ex.getCode());
    }

    @Test
    void serverErrorCodeMaxValue() {
        var ex = new ServerErrorException(Integer.MAX_VALUE, "max");
        assertEquals(Integer.MAX_VALUE, ex.getCode());
    }

    @Test
    void serverErrorCodeMinValue() {
        var ex = new ServerErrorException(Integer.MIN_VALUE, "min");
        assertEquals(Integer.MIN_VALUE, ex.getCode());
    }

    @Test
    void serverErrorNullMessage() {
        var ex = new ServerErrorException(1, null);
        assertNull(ex.getMessage());
    }

    @Test
    void serverErrorCheckedExceptionCause() {
        var cause = new IOException("IO error");
        var ex = new ServerErrorException(1, "wrapped", cause);
        assertSame(cause, ex.getCause());
        assertInstanceOf(IOException.class, ex.getCause());
    }

    @Test
    void serverErrorStackTrace() {
        var ex = new ServerErrorException(1, "error");
        ex.fillInStackTrace();
        assertNotNull(ex.getStackTrace());
    }

    @Test
    void serverErrorMessageAccess() {
        var ex = new ServerErrorException(1, "msg");
        assertEquals("msg", ex.getMessage());
        assertTrue(ex.toString().contains("msg"));
    }
}
