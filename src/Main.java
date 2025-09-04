import java.io.File;
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
        }
    }
}