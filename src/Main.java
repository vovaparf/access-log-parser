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
                int maxLength = 0;
                int minLength = Integer.MAX_VALUE;

                while ((line = reader.readLine()) != null) {
                    totalLines++;
                    int length = line.length();


                    if (length > 1024) {
                        throw new LineTooLongException(
                                "Ошибка: строка №" + totalLines + " превышает 1024 символа (длина = " + length + ")"
                        );
                    }

                    if (length > maxLength) {
                        maxLength = length;
                    }

                    if (length < minLength) {
                        minLength = length;
                    }
                }

                reader.close();
                fileReader.close();

                System.out.println("Общее количество строк: " + totalLines);
                System.out.println("Длина самой длинной строки: " + maxLength);
                System.out.println("Длина самой короткой строки: " + (minLength == Integer.MAX_VALUE ? 0 : minLength));

            } catch (LineTooLongException ex) {
                System.err.println(ex.getMessage());
                break;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}