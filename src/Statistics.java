import java.time.LocalDateTime;
import java.time.Duration;

public class Statistics {
    private int totalTraffic;
    private LocalDateTime minTime;
    private LocalDateTime maxTime;

    public Statistics() {
        this.totalTraffic = 0;
        this.minTime = null;
        this.maxTime = null;
    }

    public void addEntry(LogEntry entry) {
        totalTraffic += entry.getResponseSize();
        LocalDateTime time = entry.getTime();

        if (minTime == null || time.isBefore(minTime)) minTime = time;
        if (maxTime == null || time.isAfter(maxTime)) maxTime = time;
    }

    public double getTrafficRate() {
        if (minTime == null || maxTime == null || minTime.equals(maxTime)) return totalTraffic;

        long hours = Duration.between(minTime, maxTime).toHours();
        if (hours == 0) hours = 1;
        return totalTraffic / (double) hours;
    }

    public int getTotalTraffic() { return totalTraffic; }
}