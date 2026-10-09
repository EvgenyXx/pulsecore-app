package ru.pulsecore.app.shop.api.dto.response;

public record OrderProblem(
        String productName,
        String variantLabel,
        Integer available,
        ProblemType type
) {
    public enum ProblemType { OUT_OF_STOCK, STOCK_SHORTAGE, INACTIVE }
}