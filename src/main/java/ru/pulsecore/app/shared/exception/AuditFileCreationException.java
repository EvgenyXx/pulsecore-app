package ru.pulsecore.app.shared.exception;

import org.springframework.http.HttpStatus;


public class AuditFileCreationException extends BaseException {
    public AuditFileCreationException(String path) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "Аудит: не удалось создать папку " + path);
    }
}