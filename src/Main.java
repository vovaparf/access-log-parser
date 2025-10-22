import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int fileCount = 0;

        while (true) {
            System.out.println("Введите путь к файлу:");
            String path = sc.nextLine();

            File file = new File(path);
            if (!file.exists() || file.isDirectory()) {
                System.out.println("Указанный путь не существует или это папка. Попробуйте снова.");
                continue;
            }

            fileCount++;
            System.out.println("Путь указан верно!");
            System.out.println("Это файл номер " + fileCount);

            Statistics stats = new Statistics();
            int lineCount = 0;

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    try {
                        lineCount++;
                        LogEntry entry = new LogEntry(line);
                        stats.addEntry(entry);
                    } catch (Exception ignored) {
                        // пропускаем некорректные строки
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                continue;
            }

            DecimalFormat df = new DecimalFormat("0.00");

            System.out.println("Всего запросов: " + stats.getTotalRequests());
            System.out.println("Доля Googlebot: " + df.format(stats.getGoogleBotShare() * 100) + "%");
            System.out.println("Доля YandexBot: " + df.format(stats.getYandexBotShare() * 100) + "%");
            System.out.println("Средний часовой трафик: " + df.format(stats.getAverageHourlyTraffic()) + " байт/час");
            System.out.println("Среднее количество ошибочных запросов в час: " + df.format(stats.getAverageErrorRequestsPerHour()));
            System.out.println("Средняя посещаемость одним пользователем: " + df.format(stats.getAverageVisitsPerUser()));

            // Выводим статистику ОС
            System.out.println("\nСтатистика операционных систем:");
            stats.getOperatingSystemShare().forEach((os, share) ->
                    System.out.println(os + ": " + df.format(share * 100) + "%"));

            // Выводим статистику браузеров
            System.out.println("\nСтатистика браузеров:");
            stats.getBrowserShare().forEach((browser, share) ->
                    System.out.println(browser + ": " + df.format(share * 100) + "%"));

            // Выводим первые 10 существующих страниц (код 200)
            List<String> pages200 = new ArrayList<>(stats.getExistingPages());
            System.out.println("\nСтраницы с кодом 200 (первые 10):");
            pages200.stream().limit(5).forEach(p -> System.out.println(" - " + p));
            System.out.println("... (всего страниц: " + pages200.size() + ")");

            // Выводим первые 10 несуществующих страниц (код 404)
            List<String> pages404 = new ArrayList<>(stats.getNotFoundPages());
            System.out.println("\nСтраницы с кодом 404 (первые 10):");
            pages404.stream().limit(5).forEach(p -> System.out.println(" - " + p));
            System.out.println("... (всего страниц: " + pages404.size() + ")\n");
        }
    }
}
