package ru.pulsecore.app.shared.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.pulsecore.app.shared.exception.BaseException;
import ru.pulsecore.app.shared.exception.dto.ErrorResponse;
import ru.pulsecore.app.shared.exception.factory.ErrorResponseFactory;
import ru.pulsecore.app.shared.exception.helper.DatabaseExceptionHelper;

@Slf4j
@RestControllerAdvice
public class DatabaseExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String constraintName = "unknown";
        if (ex.getCause() instanceof ConstraintViolationException cve) {
            constraintName = cve.getConstraintName();
        }

        BaseException baseException = DatabaseExceptionHelper.translate(ex);

        log.warn("Database constraint violated: [{}] -> {} (HTTP {})", constraintName, baseException.getMessage(), baseException.getStatus().value());

        return ErrorResponseFactory.build(baseException);
    }
}