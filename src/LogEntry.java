import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class LogEntry {
    private final String ipAddr;
    private final LocalDateTime time;
    private final HttpMethod method;
    private final String path;
    private final int responseCode;
    private final int responseSize;
    private final String referer;
    private final UserAgent userAgent;

    public LogEntry(String logLine) {
        String[] parts = logLine.split("\"");


        String[] left = parts[0].trim().split(" ");
        this.ipAddr = left[0];

        int open = logLine.indexOf('[');
        int close = logLine.indexOf(']');
        String dateStr = logLine.substring(open + 1, close);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);
        this.time = ZonedDateTime.parse(dateStr, formatter).toLocalDateTime();


        String[] requestParts = parts[1].split(" ");
        this.method = HttpMethod.fromString(requestParts[0]);
        this.path = requestParts.length > 1 ? requestParts[1] : "-";


        String[] statusAndSize = parts[2].trim().split(" ");
        this.responseCode = Integer.parseInt(statusAndSize[0]);
        this.responseSize = (statusAndSize.length > 1 && !statusAndSize[1].equals("-"))
                ? Integer.parseInt(statusAndSize[1]) : 0;


        this.referer = parts.length > 3 ? parts[3] : "-";
        String userAgentStr = parts.length > 5 ? parts[5] : "-";
        this.userAgent = new UserAgent(userAgentStr);
    }

    public String getIpAddr() { return ipAddr; }
    public LocalDateTime getTime() { return time; }
    public HttpMethod getMethod() { return method; }
    public String getPath() { return path; }
    public int getResponseCode() { return responseCode; }
    public int getResponseSize() { return responseSize; }
    public String getReferer() { return referer; }
    public UserAgent getUserAgent() { return userAgent; }
}