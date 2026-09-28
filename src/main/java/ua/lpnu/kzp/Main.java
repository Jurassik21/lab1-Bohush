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
            try {
                // Вся логіка розбору та валідації тепер інкапсульована у класі Lesson
                Lesson lesson = Lesson.fromCsv(lines.get(index));
                
                validCount++;
                minRoom = Math.min(minRoom, lesson.getRoom());
                maxDuration = Math.max(maxDuration, lesson.getDurationMinutes());
                totalDuration += lesson.getDurationMinutes();
                
            } catch (IllegalArgumentException exception) {
                // Перехоплюємо помилку та зберігаємо оригінальний номер рядка
                errors.add(String.format(Locale.ROOT, "Рядок %d: %s", index + 1, exception.getMessage()));
            }
        }

        // Упаковуємо фінальні дані у незмінний record RoomDuration
        RoomDuration summary = new RoomDuration(
            validCount,
            validCount == 0 ? 0 : minRoom,
            maxDuration,
            totalDuration
        );

        StringBuilder reportBuilder = new StringBuilder();
        reportBuilder.append(String.format(Locale.ROOT, "Коректних записів: %d%n", summary.validCount()));

        if (summary.validCount() > 0) {
            reportBuilder.append(String.format(Locale.ROOT, "Найменший номер аудиторії: %d%n", summary.minRoom()));
            reportBuilder.append(String.format(Locale.ROOT, "Найдовше заняття: %d хв%n", summary.maxDuration()));
            reportBuilder.append(String.format(Locale.ROOT, "Сумарна тривалість: %d хв%n", summary.totalDuration()));
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