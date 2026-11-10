package ru.pulsecore.app.shared.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class SleepUtils {

    /**
     * Приостанавливает текущий поток на указанное количество секунд.
     * Если поток прерван, восстанавливает флаг прерывания и выходит.
     */
    public void sleepSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}