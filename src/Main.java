import java.io.*;
import java.util.Map;
import java.util.Scanner;

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

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                int totalLines = 0;
                int googleCount = 0;
                int yandexCount = 0;

                Statistics stats = new Statistics();

                while ((line = reader.readLine()) != null) {
                    totalLines++;

                    if (line.length() > 1024) {
                        throw new LineTooLongException(
                                "Ошибка: строка №" + totalLines +
                                        " превышает 1024 символа (длина = " + line.length() + ")"
                        );
                    }

                    LogEntry entry = new LogEntry(line);
                    stats.addEntry(entry);

                    String browser = entry.getUserAgent().getBrowser();
                    if (browser.equalsIgnoreCase("Googlebot")) {
                        googleCount++;
                    } else if (browser.equalsIgnoreCase("YandexBot")) {
                        yandexCount++;
                    }
                }


                System.out.println("Всего запросов: " + totalLines);
                if (totalLines > 0) {
                    double googleShare = (googleCount * 100.0) / totalLines;
                    double yandexShare = (yandexCount * 100.0) / totalLines;
                    System.out.printf("Доля Googlebot: %.2f%%%n", googleShare);
                    System.out.printf("Доля YandexBot: %.2f%%%n", yandexShare);
                }

                double avgTraffic = stats.getTrafficRate();

                System.out.println("\nСтраницы с кодом 404 (первые 10):");
                stats.getMissingPages().stream().limit(5).forEach(p -> System.out.println(" - " + p));
                System.out.println("... (всего: " + stats.getMissingPages().size() + ")");

                System.out.println("\nСтраницы с кодом 200 (первые 10):");
                int shown = 0;
                for (String page : stats.getExistingPages()) {
                    System.out.println(" - " + page);
                    shown++;
                    if (shown >= 5) break;
                }
                System.out.println("... (всего страниц: " + stats.getExistingPages().size() + ")");


                System.out.println("\nСтатистика операционных систем:");
                for (Map.Entry<String, Double> entry : stats.getOSStatistics().entrySet()) {
                    System.out.printf("%s: %.2f%%%n", entry.getKey(), entry.getValue() * 100);
                }

            } catch (LineTooLongException ex) {
                System.err.println(ex.getMessage());
                break;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}