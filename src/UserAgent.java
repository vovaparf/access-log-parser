public class UserAgent {
    private final String fullAgent;
    private final String operatingSystem;
    private final String browser;

    public UserAgent(String fullAgent) {
        this.fullAgent = fullAgent == null ? "-" : fullAgent;
        String uaLower = fullAgent.toLowerCase();

        // Определяем ОС
        if (uaLower.contains("windows")) {
            this.operatingSystem = "Windows";
        } else if (uaLower.contains("mac")) {
            this.operatingSystem = "macOS";
        } else if (uaLower.contains("linux") || uaLower.contains("android")) {
            this.operatingSystem = "Linux";
        } else {
            this.operatingSystem = "Other";
        }

        // Определяем браузер
        if (uaLower.contains("chrome")) {
            this.browser = "Chrome";
        } else if (uaLower.contains("firefox")) {
            this.browser = "Firefox";
        } else if (uaLower.contains("opera")) {
            this.browser = "Opera";
        } else if (uaLower.contains("edge")) {
            this.browser = "Edge";
        } else if (uaLower.contains("safari")) {
            this.browser = "Safari";
        } else if (uaLower.contains("bot")) {
            this.browser = "Bot";
        } else {
            this.browser = "Other";
        }
    }

    public String getFullAgent() {
        return fullAgent;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public String getBrowser() {
        return browser;
    }

    public boolean isBot() {
        return fullAgent.toLowerCase().contains("bot");
    }
}
