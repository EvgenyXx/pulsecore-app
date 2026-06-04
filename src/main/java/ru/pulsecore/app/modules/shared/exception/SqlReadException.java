package ru.pulsecore.app.modules.shared.exception;

public class SqlReadException extends RuntimeException {
    public SqlReadException(String path, Throwable cause) {
        super("Cannot read SQL: " + path, cause);
    }
}