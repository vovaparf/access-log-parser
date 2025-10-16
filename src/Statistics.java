import java.time.LocalDateTime;
import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Statistics {
    private int totalTraffic;
    private LocalDateTime minTime;
    private LocalDateTime maxTime;


    private final Set<String> existingPages;
    private final Map<String, Integer> osCounts;

    public Statistics() {
        this.totalTraffic = 0;
        this.minTime = null;
        this.maxTime = null;
        this.existingPages = new HashSet<>();
        this.osCounts = new HashMap<>();
    }

    public void addEntry(LogEntry entry) {
        totalTraffic += entry.getResponseSize();
        LocalDateTime time = entry.getTime();


        if (minTime == null || time.isBefore(minTime)) minTime = time;
        if (maxTime == null || time.isAfter(maxTime)) maxTime = time;


        if (entry.getResponseCode() == 200) {
            existingPages.add(entry.getPath());
        }


        String os = entry.getUserAgent().getOperatingSystem();
        osCounts.put(os, osCounts.getOrDefault(os, 0) + 1);
    }


    public double getTrafficRate() {
        if (minTime == null || maxTime == null || minTime.equals(maxTime)) return totalTraffic;
        long hours = Duration.between(minTime, maxTime).toHours();
        if (hours == 0) hours = 1;
        return totalTraffic / (double) hours;
    }


    public Set<String> getExistingPages() {
        return existingPages;
    }


    public Map<String, Double> getOSStatistics() {
        Map<String, Double> osShare = new HashMap<>();
        int total = osCounts.values().stream().mapToInt(Integer::intValue).sum();

        for (Map.Entry<String, Integer> entry : osCounts.entrySet()) {
            double share = total > 0 ? (entry.getValue() * 1.0 / total) : 0.0;
            osShare.put(entry.getKey(), share);
        }

        return osShare;
    }

    public int getTotalTraffic() {
        return totalTraffic;
    }
}