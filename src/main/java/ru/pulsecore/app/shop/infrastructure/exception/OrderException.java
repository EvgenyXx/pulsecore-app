package ru.pulsecore.app.shop.infrastructure.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import ru.pulsecore.app.shared.exception.BaseException;
import ru.pulsecore.app.shop.api.dto.response.OrderProblem;

import java.util.List;

@Getter
public class OrderException extends BaseException {

    private final List<OrderProblem> problems;

    public OrderException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
        this.problems = List.of();
    }

    public OrderException(String message, List<OrderProblem> problems) {
        super(HttpStatus.BAD_REQUEST, message);
        this.problems = problems != null ? problems : List.of();
    }

}