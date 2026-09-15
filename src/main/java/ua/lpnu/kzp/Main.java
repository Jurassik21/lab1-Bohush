package ua.lpnu.kzp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Головний клас програми для обробки записів розкладу занять.
 */
public final class Main {

    /* Забороняє створення екземплярів службового класу. */
    private Main() {
    }

    /**
     * Точка входу до програми. Читає файл, перевіряє записи та формує звіт.
     * Підтримує аргументи командного рядка: --help, --version, --input, --output.
     *
     * @param args аргументи командного рядка
     */
    public static void main(String[] args) {
        Path input = Path.of("data", "input.csv");
        Path outDir = Path.of("out");
        Path output = outDir.resolve("report.txt");

        // Обробка аргументів командного рядка
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--help")) {
                System.out.println("Використання: java -jar target/lab01-bohush-1.0.0.jar [опції]");
                System.out.println("Опції:");
                System.out.println("  --help             Показати цю довідку");
                System.out.println("  --version          Показати версію програми");
                System.out.println("  --input <шлях>     Шлях до вхідного файлу CSV");
                System.out.println("  --output <шлях>    Шлях до вихідного файлу звіту");
                return;
            } else if (args[i].equals("--version")) {
                System.out.println("v1.0.0");
                return;
            } else if (args[i].equals("--input") && i + 1 < args.length) {
                input = Path.of(args[++i]);
            } else if (args[i].equals("--output") && i + 1 < args.length) {
                output = Path.of(args[++i]);
                if (output.getParent() != null) {
                    outDir = output.getParent(); 
                } else {
                    outDir = Path.of("."); // Якщо вказано лише ім'я файлу без папки
                }
            }
        }

        List<String> lines;
        try {
            // Читаємо файл із явним кодуванням UTF-8
            lines = Files.readAllLines(input, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("Помилка читання вхідного файлу: " + e.getMessage());
            return;
        }

        List<String> errors = new ArrayList<>();
        int validCount = 0;
        int minRoom = Integer.MAX_VALUE;
        int maxDuration = 0;
        int totalDuration = 0;

        for (int index = 0; index < lines.size(); index++) {
            String[] fields = lines.get(index).split(";", -1);

            if (fields.length != 5) {
                errors.add(String.format(Locale.ROOT, "Рядок %d: очікується 5 полів", index + 1));
                continue;
            }

            if (fields[0].isBlank() || fields[1].isBlank() || fields[2].isBlank()) {
                errors.add(String.format(Locale.ROOT, "Рядок %d: порожнє текстове поле", index + 1));
                continue;
            }

            try {
                int room = Integer.parseInt(fields[3].trim());
                int duration = Integer.parseInt(fields[4].trim());

                if (room < 0 || duration < 0) {
                    errors.add(String.format(Locale.ROOT, "Рядок %d: від'ємне числове значення", index + 1));
                    continue;
                }

                validCount++;
                minRoom = Math.min(minRoom, room);
                maxDuration = Math.max(maxDuration, duration);
                totalDuration += duration;

            } catch (NumberFormatException exception) {
                errors.add(String.format(Locale.ROOT, "Рядок %d: числове поле має помилковий формат", index + 1));
            }
        }

        StringBuilder reportBuilder = new StringBuilder();
        reportBuilder.append(String.format(Locale.ROOT, "Коректних записів: %d%n", validCount));
        
        if (validCount > 0) {
            reportBuilder.append(String.format(Locale.ROOT, "Найменший номер аудиторії: %d%n", minRoom));
            reportBuilder.append(String.format(Locale.ROOT, "Найдовше заняття: %d хв%n", maxDuration));
            reportBuilder.append(String.format(Locale.ROOT, "Сумарна тривалість: %d хв%n", totalDuration));
        }
        
        reportBuilder.append(String.format(Locale.ROOT, "Помилок: %d%n", errors.size()));
        for (String error : errors) {
            reportBuilder.append(error).append(System.lineSeparator());
        }

        String finalReport = reportBuilder.toString();
        System.out.println(finalReport);

        try {
            // Безпечне створення директорії для SpotBugs
            if (!Files.exists(outDir)) {
                Files.createDirectories(outDir);
            }
            Files.writeString(output, finalReport, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("Помилка запису файлу звіту: " + e.getMessage());
        }
    }
}