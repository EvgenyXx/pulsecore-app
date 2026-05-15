package ru.pulsecore.app.modules.player.service.analytic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.pulsecore.app.modules.player.api.dto.DailyIncomeResponse;
import ru.pulsecore.app.modules.player.api.dto.MonthlyIncomeResponse;
import ru.pulsecore.app.modules.shared.propirties.GigaChatProperties;

import javax.net.ssl.*;
import java.security.cert.X509Certificate;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class GigaChatService {

    private final GigaChatProperties props;
    private String accessToken;
    private long tokenExpiry;
    private static final String[] monthNames = {"Январь","Февраль","Март","Апрель","Май","Июнь","Июль","Август","Сентябрь","Октябрь","Ноябрь","Декабрь"};

    public String analyze(String prompt) {
        return ask(prompt);
    }

    public String analyze(List<Map<String, String>> messages) {
        return ask(messages);
    }

    public String analyzeYear(MonthlyIncomeResponse stats, int year) {
        var months = stats.getMonths();
        if (months.isEmpty()) return "Нет данных для анализа.";

        double maxTotal = months.stream().mapToDouble(MonthlyIncomeResponse.MonthStat::getTotal).max().orElse(0);
        double minTotal = months.stream().filter(m -> m.getTotal() > 0).mapToDouble(MonthlyIncomeResponse.MonthStat::getTotal).min().orElse(0);
        double firstHalf = months.stream().filter(m -> Integer.parseInt(m.getMonth().split("-")[1]) <= 6).mapToDouble(MonthlyIncomeResponse.MonthStat::getTotal).sum();
        double secondHalf = months.stream().filter(m -> Integer.parseInt(m.getMonth().split("-")[1]) > 6).mapToDouble(MonthlyIncomeResponse.MonthStat::getTotal).sum();
        String trend = secondHalf > firstHalf ? "растёшь (+" + String.format("%.0f ₽", secondHalf - firstHalf) + ")" : "падаешь (" + String.format("%.0f ₽", secondHalf - firstHalf) + ")";

        String bestMonth = months.stream().filter(m -> m.getTotal() == maxTotal).findFirst()
                .map(m -> monthNames[Integer.parseInt(m.getMonth().split("-")[1]) - 1] + " — " + String.format("%.0f ₽ (%d турнира)", m.getTotal(), m.getCount())).orElse("—");
        String worstMonth = months.stream().filter(m -> m.getTotal() == minTotal && m.getTotal() > 0).findFirst()
                .map(m -> monthNames[Integer.parseInt(m.getMonth().split("-")[1]) - 1] + " — " + String.format("%.0f ₽ (%d турнира)", m.getTotal(), m.getCount())).orElse("—");

        long totalTournaments = months.stream().mapToLong(MonthlyIncomeResponse.MonthStat::getCount).sum();
        double avgCheck = totalTournaments > 0 ? stats.getOverallAverage() * stats.getMonths().size() / totalTournaments : 0;
        int activeMonths = (int) months.stream().filter(m -> m.getTotal() > 0).count();
        int zeroMonths = 12 - activeMonths;

        String prompt = String.format("""
                Ты — профессиональный спортивный аналитик и тренер по настольному теннису с 15-летним опытом. Твоя специализация — анализ статистики и поиск точек роста.
                
                Проведи глубокий анализ годовой статистики игрока. Обрати внимание на:
                - Динамику доходов (растёт или падает)
                - Стабильность результатов
                - Количество турниров и их влияние на доход
                - Пропущенные месяцы (если есть)
                - Соотношение среднего чека к пиковым значениям
                
                ## ДАННЫЕ ИГРОКА:
                Игрок: %s
                Год: %d
                Средний доход в месяц: %.0f ₽
                Средний чек за турнир: ~%.0f ₽
                Всего турниров: %d
                Активных месяцев: %d из 12
                Пропущено месяцев: %d
                
                ## ЛУЧШИЙ МЕСЯЦ: %s
                ## ХУДШИЙ МЕСЯЦ (из активных): %s
                ## ТРЕНД: %s
                ## ПЕРВОЕ ПОЛУГОДИЕ: %.0f ₽
                ## ВТОРОЕ ПОЛУГОДИЕ: %.0f ₽
                
                Дай ОДИН максимально конкретный, персонализированный совет. Не используй общие фразы вроде "тренируйся больше" или "следи за формой". Укажи на конкретную цифру или тренд из данных.
                Ответ: строго 2-3 предложения на русском языке.
                """,
                stats.getPlayerName(), year, stats.getOverallAverage(), avgCheck,
                totalTournaments, activeMonths, zeroMonths,
                bestMonth, worstMonth, trend, firstHalf, secondHalf
        );
        return ask(prompt);
    }

    public String analyzeMonth(DailyIncomeResponse stats, int year, int month) {
        var days = stats.getDays();
        if (days.isEmpty()) return "Нет данных для анализа.";

        double maxDay = days.stream().mapToDouble(DailyIncomeResponse.DayStat::getTotal).max().orElse(0);
        long activeDays = days.stream().filter(d -> d.getTotal() > 0).count();
        long totalTournaments = days.stream().mapToLong(DailyIncomeResponse.DayStat::getCount).sum();
        double avgPerActiveDay = activeDays > 0 ? stats.getMonthTotal() / activeDays : 0;

        String bestDay = days.stream().filter(d -> d.getTotal() == maxDay).findFirst()
                .map(d -> d.getDay() + " числа — " + String.format("%.0f ₽ (%d турнира)", d.getTotal(), d.getCount())).orElse("—");

        String prompt = String.format("""
                Ты — профессиональный спортивный аналитик и тренер по настольному теннису.
                Проведи анализ месяца игрока.
                
                ## ДАННЫЕ:
                Игрок: %s
                Месяц: %s %d
                Доход за месяц: %.0f ₽
                Средний доход в игровой день: %.0f ₽
                Активных дней: %d
                Всего турниров: %d
                Лучший день: %s
                
                Дай ОДИН конкретный совет на основе этих данных. Укажи на цифру или тренд.
                Ответ: строго 2-3 предложения на русском языке.
                """,
                stats.getPlayerName(), monthNames[month-1], year,
                stats.getMonthTotal(), avgPerActiveDay, activeDays, totalTournaments, bestDay
        );
        return ask(prompt);
    }

    public String getMonthlyAdvice(MonthlyIncomeResponse stats, int year) {
        return analyzeYear(stats, year);
    }

    private String ask(String prompt) {
        return ask(List.of(Map.of("role", "user", "content", prompt)));
    }

    private String ask(List<Map<String, String>> messages) {
        try {
            if (accessToken == null || System.currentTimeMillis() > tokenExpiry) refreshToken();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + accessToken);

            Map<String, Object> body = Map.of(
                    "model", "GigaChat",
                    "messages", messages,
                    "max_tokens", 500,
                    "temperature", 0.7
            );

            ResponseEntity<Map> response = restTemplate().postForEntity(
                    props.getApiUrl(), new HttpEntity<>(body, headers), Map.class);

            var choices = (List<Map>) response.getBody().get("choices");
            return (String) ((Map) choices.get(0).get("message")).get("content");
        } catch (Exception e) {
            log.error("GigaChat API error", e);
            return "Не удалось получить ответ. Попробуйте позже.";
        }
    }

    private void refreshToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Basic " + props.getAuthKey());
        headers.set("RqUID", UUID.randomUUID().toString());

        ResponseEntity<Map> response = restTemplate().postForEntity(
                props.getAuthUrl(),
                new HttpEntity<>("scope=" + props.getScope(), headers),
                Map.class);

        accessToken = (String) response.getBody().get("access_token");
        tokenExpiry = System.currentTimeMillis() + 30 * 60 * 1000;
    }

    private RestTemplate restTemplate() {
        try {
            TrustManager[] trustAll = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() { return null; }
                public void checkClientTrusted(X509Certificate[] c, String a) {}
                public void checkServerTrusted(X509Certificate[] c, String a) {}
            }};
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAll, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception ignored) {}
        return new RestTemplate();
    }
}