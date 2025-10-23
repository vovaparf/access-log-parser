public class UserAgent {
    private final String fullAgent;
    private final String operatingSystem;
    private final String browser;

    public UserAgent(String fullAgent) {
        this.fullAgent = fullAgent != null ? fullAgent : "";

        String os;
        if (fullAgent.contains("Windows")) os = "Windows";
        else if (fullAgent.contains("Mac")) os = "macOS";
        else if (fullAgent.contains("Linux")) os = "Linux";
        else os = "Other";
        this.operatingSystem = os;

        String br;
        if (fullAgent.contains("Chrome")) br = "Chrome";
        else if (fullAgent.contains("Firefox")) br = "Firefox";
        else if (fullAgent.contains("Opera") || fullAgent.contains("OPR")) br = "Opera";
        else if (fullAgent.contains("Edge")) br = "Edge";
        else br = "Other";
        this.browser = br;
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
}
