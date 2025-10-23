import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Statistics {

    private int totalRequests = 0;
    private long totalTraffic = 0;
    private int errorRequests = 0;

    private int realUserRequests = 0;
    private int googleBotRequests = 0;
    private int yandexBotRequests = 0;

    private LocalDateTime minTime = null;
    private LocalDateTime maxTime = null;

    private final Set<String> existingPages = new HashSet<>();
    private final Set<String> notFoundPages = new HashSet<>();

    private final Map<String, Integer> osStats = new HashMap<>();
    private final Map<String, Integer> browserStats = new HashMap<>();

    private final Map<String, Integer> visitsPerUser = new HashMap<>();
    private final Map<Integer, Integer> visitsPerSecond = new HashMap<>();
    private final Set<String> referringDomains = new HashSet<>();

    public void addEntry(LogEntry entry) {
        totalRequests++;

        LocalDateTime time = entry.getDateTime();
        if (minTime == null || time.isBefore(minTime)) minTime = time;
        if (maxTime == null || time.isAfter(maxTime)) maxTime = time;

        String agent = entry.getUserAgent().getFullAgent().toLowerCase();
        boolean isBot = agent.contains("bot");

        // --- Счётчики ботов ---
        if (agent.contains("googlebot")) googleBotRequests++;
        if (agent.contains("yandexbot")) yandexBotRequests++;

        totalTraffic += entry.getBytes();

        if (entry.getStatusCode() == 200) existingPages.add(entry.getRequestPath());
        if (entry.getStatusCode() == 404) notFoundPages.add(entry.getRequestPath());
        if (entry.getStatusCode() >= 400 && entry.getStatusCode() < 600) errorRequests++;

        if (!isBot) {
            realUserRequests++;
            osStats.merge(entry.getUserAgent().getOperatingSystem(), 1, Integer::sum);
            browserStats.merge(entry.getUserAgent().getBrowser(), 1, Integer::sum);
            visitsPerUser.merge(entry.getIpAddress(), 1, Integer::sum);

            int secondKey = time.toLocalTime().toSecondOfDay();
            visitsPerSecond.merge(secondKey, 1, Integer::sum);

            String ref = entry.getReferer();
            if (ref != null && ref.startsWith("http")) {
                Matcher matcher = Pattern.compile("https?://([^/]+)/?").matcher(ref);
                if (matcher.find()) {
                    referringDomains.add(matcher.group(1));
                }
            }
        }
    }

    // Средний трафик за час
    public double getAverageTrafficPerHour() {
        if (minTime == null || maxTime == null) return 0;
        double hours = Duration.between(minTime, maxTime).toMinutes() / 60.0;
        if (hours <= 0) hours = 1;
        return totalTraffic / hours;
    }

    // Среднее количество ошибочных запросов в час
    public double getAverageErrorsPerHour() {
        if (minTime == null || maxTime == null) return 0;
        double hours = Duration.between(minTime, maxTime).toMinutes() / 60.0;
        if (hours <= 0) hours = 1;
        return errorRequests / hours;
    }

    // Средняя посещаемость одним пользователем
    public double getAverageVisitsPerUser() {
        if (visitsPerUser.isEmpty()) return 0;
        return (double) realUserRequests / visitsPerUser.size();
    }

    // Пиковая посещаемость в секунду
    public int getPeakVisitsPerSecond() {
        return visitsPerSecond.values().stream().max(Integer::compareTo).orElse(0);
    }

    // Список доменов-рефереров
    public Set<String> getReferringDomains() {
        return referringDomains;
    }

    // Максимальная посещаемость одним пользователем
    public int getMaxVisitsByOneUser() {
        return visitsPerUser.values().stream().max(Integer::compareTo).orElse(0);
    }

    // --- Доля Googlebot / YandexBot ---
    public double getBotShare(String botName) {
        if (totalRequests == 0) return 0.0;
        if (botName.equalsIgnoreCase("googlebot")) {
            return (double) googleBotRequests / totalRequests;
        } else if (botName.equalsIgnoreCase("yandexbot")) {
            return (double) yandexBotRequests / totalRequests;
        }
        return 0.0;
    }

    public Set<String> getExistingPages() {
        return existingPages;
    }

    public Set<String> getNotFoundPages() {
        return notFoundPages;
    }

    public Map<String, Integer> getOsStats() {
        return osStats;
    }

    public Map<String, Integer> getBrowserStats() {
        return browserStats;
    }

    public long getTotalTraffic() {
        return totalTraffic;
    }

    public int getTotalRequests() {
        return totalRequests;
    }

    public int getErrorRequests() {
        return errorRequests;
    }
}
