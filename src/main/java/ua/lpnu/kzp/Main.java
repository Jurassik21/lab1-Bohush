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
     *
     * @param args аргументи командного рядка
     */
    public static void main(String[] args) {
        // Використовуємо Path для кросплатформності (працює на Windows, macOS, Ubuntu)
        Path input = Path.of("data", "input.csv");
        Path output = Path.of("out", "report.txt");

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
            // Розділяємо рядок крапкою з комою, зберігаючи порожні поля (-1)
            String[] fields = lines.get(index).split(";", -1);

            if (fields.length != 5) {
                errors.add("Рядок %d: очікується 5 полів".formatted(index + 1));
                continue;
            }

            // Перевіряємо, чи не порожні текстові обов'язкові поля (предмет, викладач, день)
            if (fields[0].isBlank() || fields[1].isBlank() || fields[2].isBlank()) {
                errors.add("Рядок %d: порожнє текстове поле".formatted(index + 1));
                continue;
            }

            try {
                // Перетворюємо числові поля
                int room = Integer.parseInt(fields[3].trim());
                int duration = Integer.parseInt(fields[4].trim());

                if (room < 0 || duration < 0) {
                    errors.add("Рядок %d: від'ємне числове значення".formatted(index + 1));
                    continue;
                }

                // Додаємо дані до статистики лише для коректних записів
                validCount++;
                minRoom = Math.min(minRoom, room);
                maxDuration = Math.max(maxDuration, duration);
                totalDuration += duration;

            } catch (NumberFormatException exception) {
                errors.add("Рядок %d: числове поле має помилковий формат".formatted(index + 1));
            }
        }

        // Формуємо текст звіту
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
        
        // Виводимо в консоль
        System.out.println(finalReport);

        // Записуємо у файл
        try {
            Files.createDirectories(output.getParent());
            Files.writeString(output, finalReport, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("Помилка запису файлу звіту: " + e.getMessage());
        }
    }
}