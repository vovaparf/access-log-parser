import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class Statistics {
    private int totalRequests = 0;
    private long totalTraffic = 0;

    private LocalDateTime minTime = null;
    private LocalDateTime maxTime = null;

    private int googleBotRequests = 0;
    private int yandexBotRequests = 0;

    private int totalErrorRequests = 0;
    private int realUserRequests = 0;

    private final Set<String> existingPages = new HashSet<>();
    private final Set<String> notFoundPages = new HashSet<>();

    private final Map<String, Integer> osCounts = new HashMap<>();
    private final Map<String, Integer> browserCounts = new HashMap<>();
    private final Set<String> realUserIPs = new HashSet<>();

    // === Основной метод добавления записи ===
    public void addEntry(LogEntry entry) {
        totalRequests++;

        // --- Обновление времени ---
        LocalDateTime time = entry.getDateTime();
        if (time != null) {
            if (minTime == null || time.isBefore(minTime)) {
                minTime = time;
            }
            if (maxTime == null || time.isAfter(maxTime)) {
                maxTime = time;
            }
        }

        // --- Подсчёт трафика ---
        totalTraffic += entry.getBytes();

        // --- Проверка на успешные страницы (код 200) ---
        if (entry.getResponseCode() == 200) {
            existingPages.add(entry.getRequestPath());
        }

        // --- Проверка на несуществующие страницы (код 404) ---
        if (entry.getResponseCode() == 404) {
            notFoundPages.add(entry.getRequestPath());
        }

        // --- Подсчёт ошибок (4xx и 5xx) ---
        if (entry.getResponseCode() >= 400 && entry.getResponseCode() < 600) {
            totalErrorRequests++;
        }

        // --- Анализ User-Agent ---
        String agent = entry.getUserAgent().getFullAgent();
        if (agent == null) return;

        boolean isBot = agent.toLowerCase().contains("bot");

        if (agent.contains("Googlebot")) googleBotRequests++;
        if (agent.contains("YandexBot")) yandexBotRequests++;

        // --- Подсчёт ОС ---
        String os = entry.getUserAgent().getOperatingSystem();
        osCounts.put(os, osCounts.getOrDefault(os, 0) + 1);

        // --- Подсчёт браузеров ---
        String browser = entry.getUserAgent().getBrowser();
        browserCounts.put(browser, browserCounts.getOrDefault(browser, 0) + 1);

        // --- Подсчёт реальных пользователей ---
        if (!isBot) {
            realUserRequests++;
            realUserIPs.add(entry.getIp());
        }
    }

    // === Методы получения долей ===

    public double getGoogleBotShare() {
        return totalRequests == 0 ? 0 : (double) googleBotRequests / totalRequests;
    }

    public double getYandexBotShare() {
        return totalRequests == 0 ? 0 : (double) yandexBotRequests / totalRequests;
    }

    // === Средний часовой трафик ===
    public double getAverageHourlyTraffic() {
        if (minTime == null || maxTime == null || minTime.equals(maxTime)) return 0;
        double hours = Math.max(1, Duration.between(minTime, maxTime).toSeconds() / 3600.0);
        return totalTraffic / hours;
    }

    // === Среднее количество ошибочных запросов в час ===
    public double getAverageErrorRequestsPerHour() {
        if (minTime == null || maxTime == null || minTime.equals(maxTime)) return 0;
        double hours = Math.max(1, Duration.between(minTime, maxTime).toSeconds() / 3600.0);
        return totalErrorRequests / hours;
    }

    // === Средняя посещаемость одним пользователем ===
    public double getAverageVisitsPerUser() {
        if (realUserIPs.isEmpty()) return 0;
        return (double) realUserRequests / realUserIPs.size();
    }

    // === Получение страниц ===
    public Set<String> getExistingPages() {
        return existingPages;
    }

    public Set<String> getNotFoundPages() {
        return notFoundPages;
    }

    // === Статистика ОС ===
    public Map<String, Double> getOperatingSystemShare() {
        Map<String, Double> result = new HashMap<>();
        int total = osCounts.values().stream().mapToInt(Integer::intValue).sum();
        if (total == 0) return result;

        for (Map.Entry<String, Integer> entry : osCounts.entrySet()) {
            result.put(entry.getKey(), (double) entry.getValue() / total);
        }
        return result;
    }

    // === Статистика браузеров ===
    public Map<String, Double> getBrowserShare() {
        Map<String, Double> result = new HashMap<>();
        int total = browserCounts.values().stream().mapToInt(Integer::intValue).sum();
        if (total == 0) return result;

        for (Map.Entry<String, Integer> entry : browserCounts.entrySet()) {
            result.put(entry.getKey(), (double) entry.getValue() / total);
        }
        return result;
    }

    // === Общее количество запросов ===
    public int getTotalRequests() {
        return totalRequests;
    }
}
