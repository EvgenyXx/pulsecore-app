package ru.pulsecore.app.shared.exception;

public class SqlReadException extends RuntimeException {
    public SqlReadException(String path, Throwable cause) {
        super("Cannot read SQL: " + path, cause);
    }
}