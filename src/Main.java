import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите путь к файлу access.log: ");
        String path = scanner.nextLine();

        Path filePath = Paths.get(path);
        if (!Files.exists(filePath)) {
            System.out.println("Файл не найден!");
            return;
        }

        Statistics stats = new Statistics();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            int lineCount = 0;

            while ((line = reader.readLine()) != null) {
                lineCount++;
                LogEntry entry = parseLogLine(line, formatter);
                if (entry != null) {
                    stats.addEntry(entry);
                }
            }

            System.out.println("Прочитано строк: " + lineCount);
            System.out.println("Всего запросов: " + stats.getTotalRequests());

            System.out.printf("Доля Googlebot: %.2f%%%n", stats.getBotShare("googlebot") * 100);
            System.out.printf("Доля YandexBot: %.2f%%%n", stats.getBotShare("yandexbot") * 100);

            System.out.printf("Средний часовой трафик: %.2f байт/час%n", stats.getAverageTrafficPerHour());
            System.out.printf("Среднее количество ошибочных запросов в час: %.2f%n", stats.getAverageErrorsPerHour());
            System.out.printf("Средняя посещаемость одним пользователем: %.2f%n", stats.getAverageVisitsPerUser());
            System.out.printf("Пиковая посещаемость в секунду: %d%n", stats.getPeakVisitsPerSecond());
            System.out.printf("Максимальная посещаемость одним пользователем: %d%n", stats.getMaxVisitsByOneUser());

            System.out.println("\nСтраницы с кодом 200 (первые 10):");
            stats.getExistingPages().stream().limit(10).forEach(p -> System.out.println(" - " + p));
            System.out.println("... (всего страниц: " + stats.getExistingPages().size() + ")");

            System.out.println("\nСтраницы с кодом 404 (первые 10):");
            stats.getNotFoundPages().stream().limit(10).forEach(p -> System.out.println(" - " + p));
            System.out.println("... (всего страниц: " + stats.getNotFoundPages().size() + ")");

            System.out.println("\nОперационные системы:");
            printPercentageMap(stats.getOsStats());

            System.out.println("\nБраузеры:");
            printPercentageMap(stats.getBrowserStats());

            System.out.println("\nРефереры (доменные имена):");
            stats.getReferringDomains().stream().limit(10).forEach(r -> System.out.println(" - " + r));
            System.out.println("... (всего уникальных: " + stats.getReferringDomains().size() + ")");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static LogEntry parseLogLine(String line, DateTimeFormatter formatter) {
        try {
            String regex = "^(\\S+) - - \\[(.+?)] \"(\\S+) (\\S+) (\\S+)\" (\\d{3}) (\\d+|-) \"(.*?)\" \"(.*?)\"$";
            var matcher = java.util.regex.Pattern.compile(regex).matcher(line);
            if (!matcher.find()) return null;

            String ip = matcher.group(1);
            LocalDateTime date = LocalDateTime.parse(matcher.group(2), formatter);
            String method = matcher.group(3);
            String path = matcher.group(4);
            int status = Integer.parseInt(matcher.group(6));
            long bytes = matcher.group(7).equals("-") ? 0 : Long.parseLong(matcher.group(7));
            String referer = matcher.group(8);
            String userAgentString = matcher.group(9);

            UserAgent agent = new UserAgent(userAgentString);
            return new LogEntry(ip, date, method, path, status, bytes, referer, agent);

        } catch (Exception e) {
            return null;
        }
    }

    private static void printPercentageMap(Map<String, Integer> map) {
        int total = map.values().stream().mapToInt(i -> i).sum();
        if (total == 0) {
            System.out.println("Нет данных.");
            return;
        }
        for (var entry : map.entrySet()) {
            double percent = entry.getValue() * 100.0 / total;
            System.out.printf(" - %s: %.2f%%%n", entry.getKey(), percent);
        }
    }
}
