import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Statistics {
    private int totalTraffic;
    private LocalDateTime minTime;
    private LocalDateTime maxTime;


    private final Set<String> existingPages = new HashSet<>();
    private final Set<String> missingPages = new HashSet<>();


    private final Map<String, Integer> osCounts = new HashMap<>();
    private final Map<String, Integer> browserCounts = new HashMap<>();

    public Statistics() {
        totalTraffic = 0;
        minTime = null;
        maxTime = null;
    }


    public void addEntry(LogEntry entry) {
        if (entry == null) return;


        totalTraffic += entry.getResponseSize();


        LocalDateTime entryTime = entry.getDateTime();
        if (minTime == null || entryTime.isBefore(minTime)) {
            minTime = entryTime;
        }
        if (maxTime == null || entryTime.isAfter(maxTime)) {
            maxTime = entryTime;
        }


        int code = entry.getResponseCode();
        if (code == 200) {
            existingPages.add(entry.getPath());
        } else if (code == 404) {
            missingPages.add(entry.getPath());
        }


        String os = entry.getUserAgent().getOperatingSystem();
        if (os == null) os = "Other";
        osCounts.put(os, osCounts.getOrDefault(os, 0) + 1);


        String browser = entry.getUserAgent().getBrowser();
        if (browser == null) browser = "Other";
        browserCounts.put(browser, browserCounts.getOrDefault(browser, 0) + 1);
    }


    public double getTrafficRate() {
        if (minTime == null || maxTime == null) return 0.0;


        if (minTime.isAfter(maxTime)) {
            LocalDateTime tmp = minTime;
            minTime = maxTime;
            maxTime = tmp;
        }

        long hours = Duration.between(minTime, maxTime).toHours();
        if (hours <= 0) hours = 1;

        double avgTraffic = (double) totalTraffic / hours;
        DecimalFormat df = new DecimalFormat("#,###.##");
        System.out.println("Средний часовой трафик: " + df.format(avgTraffic) + " байт/час");
        return avgTraffic;
    }


    public Set<String> getExistingPages() {
        return existingPages;
    }


    public Set<String> getMissingPages() {
        return missingPages;
    }


    public Map<String, Double> getOSStatistics() {
        Map<String, Double> result = new HashMap<>();
        int total = osCounts.values().stream().mapToInt(Integer::intValue).sum();
        if (total == 0) return result;

        for (Map.Entry<String, Integer> entry : osCounts.entrySet()) {
            result.put(entry.getKey(), entry.getValue() / (double) total);
        }
        return result;
    }


    public Map<String, Double> getBrowserStatistics() {
        Map<String, Double> result = new HashMap<>();
        int total = browserCounts.values().stream().mapToInt(Integer::intValue).sum();
        if (total == 0) return result;

        for (Map.Entry<String, Integer> entry : browserCounts.entrySet()) {
            result.put(entry.getKey(), entry.getValue() / (double) total);
        }
        return result;
    }
}
