package ru.pulsecore.app.tournament.infrastructure.gate;

import org.springframework.stereotype.Component;

import java.util.concurrent.Semaphore;
import java.util.function.Supplier;

@Component
public class MastersApiGate {

    private final Semaphore semaphore = new Semaphore(1);

    public <T> T execute(Supplier<T> task) {
        try {
            semaphore.acquire();
            try {
                return task.get();
            } finally {
                semaphore.release();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Прервано ожидание доступа к Masters API", e);
        }
    }

    public void execute(Runnable task) {
        execute(() -> {
            task.run();
            return null;
        });
    }
}