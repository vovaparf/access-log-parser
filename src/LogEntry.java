import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogEntry {
    private final String ip;
    private final LocalDateTime dateTime;
    private final HttpMethod method;
    private final String path;
    private final int responseCode;
    private final int responseSize;
    private final String referer;
    private final UserAgent userAgent;


    public enum HttpMethod {
        GET, POST, PUT, DELETE, HEAD, OPTIONS, PATCH, UNKNOWN
    }

    public LogEntry(String logLine) {

        Pattern pattern = Pattern.compile(
                "^(\\S+) \\S+ \\S+ \\[(.*?)\\] \"(\\S+) (.*?) .*?\" (\\d{3}) (\\d+|-) \"(.*?)\" \"(.*?)\"$"
        );
        Matcher matcher = pattern.matcher(logLine);

        if (!matcher.find()) {
            throw new IllegalArgumentException("Неверный формат строки лога: " + logLine);
        }

        this.ip = matcher.group(1);
        String dateStr = matcher.group(2);
        String methodStr = matcher.group(3);
        this.path = matcher.group(4);
        this.responseCode = Integer.parseInt(matcher.group(5));
        String sizeStr = matcher.group(6);
        this.responseSize = sizeStr.equals("-") ? 0 : Integer.parseInt(sizeStr);
        this.referer = matcher.group(7);
        this.userAgent = new UserAgent(matcher.group(8));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateStr, formatter);
        this.dateTime = zonedDateTime.toLocalDateTime();

        HttpMethod tmpMethod;
        try {
            tmpMethod = HttpMethod.valueOf(methodStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            tmpMethod = HttpMethod.UNKNOWN;
        }
        this.method = tmpMethod;
    }


    public String getIp() {
        return ip;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public int getResponseSize() {
        return responseSize;
    }

    public String getReferer() {
        return referer;
    }

    public UserAgent getUserAgent() {
        return userAgent;
    }
}