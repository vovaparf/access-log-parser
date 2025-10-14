import java.io.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int fileCount = 0;

        while (true) {
            System.out.println("Введите путь к файлу:");
            String path = sc.nextLine();

            File file = new File(path);
            boolean fileExists = file.exists();
            boolean isDirectory = file.isDirectory();

            if (!fileExists || isDirectory) {
                System.out.println("Указанный путь не существует или это папка. Попробуйте снова.");
                continue;
            }

            fileCount++;
            System.out.println("Путь указан верно!");
            System.out.println("Это файл номер " + fileCount);


            try {
                FileReader fileReader = new FileReader(path);
                BufferedReader reader = new BufferedReader(fileReader);

                String line;
                int totalLines = 0;
                int googleCount = 0;
                int yandexCount = 0;

                while ((line = reader.readLine()) != null) {
                    totalLines++;

                    if (line.length() > 1024) {
                        throw new LineTooLongException(
                                "Ошибка: строка №" + totalLines +
                                        " превышает 1024 символа (длина = " + line.length() + ")"
                        );
                    }


                    int openIndex = line.indexOf('(');
                    int closeIndex = line.indexOf(')', openIndex + 1);

                    if (openIndex != -1 && closeIndex != -1 && closeIndex > openIndex) {
                        String firstBrackets = line.substring(openIndex + 1, closeIndex);
                        String[] parts = firstBrackets.split(";");

                        if (parts.length >= 2) {

                            String fragment = parts[1].trim();


                            int slashIndex = fragment.indexOf('/');
                            if (slashIndex != -1) {
                                fragment = fragment.substring(0, slashIndex).trim();
                            }


                            if (fragment.equalsIgnoreCase("Googlebot")) {
                                googleCount++;
                            } else if (fragment.equalsIgnoreCase("YandexBot")) {
                                yandexCount++;
                            }
                        }
                    }
                }

                reader.close();
                fileReader.close();


                System.out.println("Всего запросов: " + totalLines);
                if (totalLines > 0) {
                    double googleShare = (googleCount * 100.0) / totalLines;
                    double yandexShare = (yandexCount * 100.0) / totalLines;
                    System.out.printf("Доля Googlebot: %.2f%%\n", googleShare);
                    System.out.printf("Доля YandexBot: %.2f%%\n", yandexShare);
                } else {
                    System.out.println("Файл пуст или не содержит корректных строк.");
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