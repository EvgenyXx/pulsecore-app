package ru.pulsecore.app.tournament.application.match;

import ru.pulsecore.app.tournament.domain.enums.MatchStage;
import ru.pulsecore.app.tournament.domain.enums.TournamentVersion;

/**
 * Стратегия для преобразования сырых названий стадий (из JSON/HTML) в строгий тип {@link MatchStage}.
 * <p>
 * <b>Зачем это нужно:</b> Сайт лиги отдает названия стадий в разных форматах в зависимости от
 * версии турнира (например, "группа", "1/2 финала", "top_mesh_1", "3rd"). Этот интерфейс позволяет
 * инкапсулировать логику парсинга для каждого формата отдельно, не засоряя основной код.
 * <p>
 * <b>Как это работает:</b> Реализации этого интерфейса (например, {@code LegacyStageParser} и
 * {@code NewStageParser}) регистрируются как Spring-бины. При создании матча
 * {@link StageParserResolver} находит нужную реализацию на основе {@link TournamentVersion}.
 *
 * @see StageParserResolver
 */
public interface StageParser {

    /**
     * Проверяет, поддерживает ли данный парсер указанную версию турнира.
     * <p>
     * <b>Внимание:</b> Резольвер выбирает <b>первый попавшийся</b> парсер, который вернул {@code true}.
     * Убедитесь, что только один бин поддерживает конкретную версию, иначе поведение может быть
     * непредсказуемым.
     *
     * @param version версия турнира (например, {@link TournamentVersion#STANDARD})
     * @return {@code true}, если парсер умеет обрабатывать этот формат, иначе {@code false}
     */
    boolean supports(TournamentVersion version);

    /**
     * Преобразует сырую строку со стадией (полученную из JSON или HTML) в строгий enum {@link MatchStage}.
     * <p>
     * <b>Контракт (обязательства):</b>
     * <ul>
     *   <li>Метод <b>никогда не должен возвращать null</b>.</li>
     *   <li>Если стадия не распознана, реализация <b>обязана вернуть</b> {@link MatchStage#GROUP}
     *       (значение по умолчанию). Это защищает систему от падения (NullPointerException)
     *       при обработке старых или нестандартных турниров.</li>
     * </ul>
     * <p>
     * <b>Примеры:</b>
     * <pre>
     * "Финал"         → MatchStage.FINAL
     * "1/2 финала"    → MatchStage.SEMIFINAL
     * "top_mesh_1"    → MatchStage.TOP_MESH_1
     * "неизвестно"    → MatchStage.GROUP
     * </pre>
     *
     * @param stage сырая строка со стадией (может быть null или пустой)
     * @return распознанная стадия матча для сохранения в БД (никогда не null)
     */
    MatchStage parse(String stage);
}