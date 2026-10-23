package ru.pulsecore.app.shared.exception.handler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import org.apache.catalina.connector.ClientAbortException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import ru.pulsecore.app.shared.exception.BaseException;
import ru.pulsecore.app.shared.exception.dto.ErrorResponse;
import ru.pulsecore.app.shared.exception.factory.ErrorResponseFactory;
import ru.pulsecore.app.shared.exception.helper.ValidationExceptionHelper;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AsyncRequestNotUsableException.class)
    public void handleAsyncRequestNotUsable(AsyncRequestNotUsableException e) {
        log.debug("Клиент прервал соединение: {}", e.getMessage());
    }


    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Void> handleMaxSize(MaxUploadSizeExceededException ex) {
        log.warn("Файл превышает лимит: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).build();
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<Void> handleMultipart(MultipartException ex) {
        log.warn("Multipart error: {}", ex.getMessage());
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        log.warn("405 {} {} -> {}", e.getMethod(), e.getMessage(), e.getSupportedHttpMethods());
        return ErrorResponseFactory.build(HttpStatus.METHOD_NOT_ALLOWED, "Метод не поддерживается");
    }

    @ExceptionHandler(ClientAbortException.class)
    public void handleClientAbort(ClientAbortException e) {
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        String message = ValidationExceptionHelper.extractMessage(e);
        log.warn("Ошибка валидации: {}", message);
        return ErrorResponseFactory.build(HttpStatus.BAD_REQUEST, message);
    }


    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Void> handleNoResource(NoResourceFoundException e) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBase(BaseException e, HttpServletRequest request) {
        log.warn("{} {} -> {} (HTTP {})", request.getMethod(), request.getRequestURI(),
                e.getMessage(), e.getStatus().value());
        return ErrorResponseFactory.build(e);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception e, HttpServletRequest request) {
        log.error("Внутренняя ошибка: {} {}", request.getMethod(), request.getRequestURI(), e);
        return ErrorResponseFactory.build(HttpStatus.INTERNAL_SERVER_ERROR, "Внутренняя ошибка сервера");
    }


}