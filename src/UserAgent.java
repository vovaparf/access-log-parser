public class UserAgent {
    private final String operatingSystem;
    private final String browser;

    public UserAgent(String userAgentString) {
        String os = "Other";
        String br = "Other";

        if (userAgentString != null) {
            String ua = userAgentString.toLowerCase();


            if (ua.contains("windows")) os = "Windows";
            else if (ua.contains("mac os") || ua.contains("macintosh") || ua.contains("macos")) os = "macOS";
            else if (ua.contains("linux")) os = "Linux";


            if (ua.contains("googlebot")) br = "Googlebot";
            else if (ua.contains("yandexbot") || ua.contains("yandex")) br = "YandexBot";
            else if (ua.contains("edg/") || ua.contains("edge")) br = "Edge";
            else if (ua.contains("opr/") || ua.contains("opera")) br = "Opera";
            else if (ua.contains("chrome") && !ua.contains("edg") && !ua.contains("opr")) br = "Chrome";
            else if (ua.contains("firefox")) br = "Firefox";
            else if (ua.contains("safari") && !ua.contains("chrome")) br = "Safari";
        }

        this.operatingSystem = os;
        this.browser = br;
    }


    public String getOperatingSystem() {
        return operatingSystem;
    }

    public String getOs() {
        return operatingSystem;
    }

    public String getBrowser() {
        return browser;
    }
}