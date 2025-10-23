import java.time.LocalDateTime;

public class LogEntry {
    private final String ipAddress;
    private final LocalDateTime dateTime;
    private final String method;
    private final String requestPath;
    private final int statusCode;
    private final long bytes;
    private final String referer;
    private final UserAgent userAgent;

    public LogEntry(String ipAddress, LocalDateTime dateTime, String method,
                    String requestPath, int statusCode, long bytes, String referer, UserAgent userAgent) {
        this.ipAddress = ipAddress;
        this.dateTime = dateTime;
        this.method = method;
        this.requestPath = requestPath;
        this.statusCode = statusCode;
        this.bytes = bytes;
        this.referer = referer;
        this.userAgent = userAgent;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getMethod() {
        return method;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public long getBytes() {
        return bytes;
    }

    public String getReferer() {
        return referer;
    }

    public UserAgent getUserAgent() {
        return userAgent;
    }
}
