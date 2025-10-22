import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class LogEntry {
    private final String ip;
    private final LocalDateTime dateTime;
    private final String method;
    private final String requestPath;
    private final int responseCode;
    private final int bytes;
    private final String referer;
    private final UserAgent userAgent;

    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);

    public LogEntry(String logLine) {
        String[] parts = logLine.split(" ");
        this.ip = parts[0];

        // Извлекаем дату между [ ]
        int dateStart = logLine.indexOf('[') + 1;
        int dateEnd = logLine.indexOf(']');
        String dateStr = logLine.substring(dateStart, dateEnd);
        this.dateTime = LocalDateTime.parse(dateStr, formatter);

        // Извлекаем метод и путь между первыми кавычками
        String[] quotes = logLine.split("\"");
        String request = quotes.length > 1 ? quotes[1] : "";
        String[] reqParts = request.split(" ");
        this.method = reqParts.length > 0 ? reqParts[0] : "";
        this.requestPath = reqParts.length > 1 ? reqParts[1] : "";

        // Код и количество байт идут после второй кавычки
        int secondQuote = logLine.indexOf('"', logLine.indexOf('"') + 1);
        String afterRequest = logLine.substring(secondQuote + 1).trim();
        String[] codeBytes = afterRequest.split(" ");
        int code = 0, bytesCount = 0;
        try {
            code = Integer.parseInt(codeBytes[0]);
            bytesCount = Integer.parseInt(codeBytes[1]);
        } catch (Exception e) {
            // Иногда бывает "-"
        }
        this.responseCode = code;
        this.bytes = bytesCount;

        // Referer — третья кавычка
        this.referer = quotes.length > 3 ? quotes[3] : "-";

        // User-Agent — пятая кавычка
        String ua = quotes.length > 5 ? quotes[5] : "-";
        this.userAgent = new UserAgent(ua);
    }

    public String getIp() {
        return ip;
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

    public int getResponseCode() {
        return responseCode;
    }

    public int getBytes() {
        return bytes;
    }

    public String getReferer() {
        return referer;
    }

    public UserAgent getUserAgent() {
        return userAgent;
    }
}
