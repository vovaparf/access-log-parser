public class UserAgent {
    private final String operatingSystem;
    private final String browser;

    public UserAgent(String userAgentString) {
        this.operatingSystem = detectOS(userAgentString);
        this.browser = detectBrowser(userAgentString);
    }

    private String detectOS(String ua) {
        ua = ua.toLowerCase();
        if (ua.contains("windows")) return "Windows";
        if (ua.contains("mac os") || ua.contains("macintosh")) return "macOS";
        if (ua.contains("linux")) return "Linux";
        return "Other";
    }

    private String detectBrowser(String ua) {
        ua = ua.toLowerCase();
        if (ua.contains("edg") || ua.contains("edge")) return "Edge";
        if (ua.contains("chrome") && !ua.contains("edg")) return "Chrome";
        if (ua.contains("firefox")) return "Firefox";
        if (ua.contains("opera") || ua.contains("opr")) return "Opera";
        if (ua.contains("safari") && !ua.contains("chrome")) return "Safari";
        return "Other";
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public String getBrowser() {
        return browser;
    }
}
