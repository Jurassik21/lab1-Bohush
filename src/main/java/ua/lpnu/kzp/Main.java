package ua.lpnu.kzp;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class Main {
    private Main() {}

    public static void main(String[] args) {
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, StandardCharsets.UTF_8));
        Path input = Path.of("data", "input.csv");
        Path output = Path.of("out", "report.txt");

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--input") && i + 1 < args.length) input = Path.of(args[i + 1]);
            if (args[i].equals("--output") && i + 1 < args.length) output = Path.of(args[i + 1]);
        }

        List<String> lines;
        try {
            lines = Files.readAllLines(input, StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.out.println("Помилка читання файлу: " + e.getMessage());
            return;
        }

        List<String> errors = new ArrayList<>();
        int validCount = 0;
        int minRoom = Integer.MAX_VALUE;
        int maxDuration = 0;
        int totalDuration = 0;
        double totalResources = 0.0;

        for (int index = 0; index < lines.size(); index++) {
            try {
                Lesson lesson = Lesson.fromCsv(lines.get(index));
                validCount++;
                minRoom = Math.min(minRoom, lesson.getRoom());
                maxDuration = Math.max(maxDuration, lesson.getDurationMinutes());
                totalDuration += lesson.getDurationMinutes();
                
                // Поліморфний виклик: Java автоматично визначає, чи це лекція, чи практика
                totalResources += lesson.calculateResourceNeeds();
            } catch (IllegalArgumentException exception) {
                errors.add(String.format(Locale.ROOT, "Рядок %d: %s", index + 1, exception.getMessage()));
            }
        }

        RoomDuration summary = new RoomDuration(
            validCount, 
            validCount == 0 ? 0 : minRoom, 
            maxDuration, 
            totalDuration, 
            totalResources
        );

        StringBuilder reportBuilder = new StringBuilder();
        if (summary.validCount() > 0) {
            reportBuilder.append(String.format(Locale.ROOT, "Коректних записів: %d%n", summary.validCount()));
            reportBuilder.append(String.format(Locale.ROOT, "Найменший номер аудиторії: %d%n", summary.minRoom()));
            reportBuilder.append(String.format(Locale.ROOT, "Найдовше заняття: %d хв%n", summary.maxDuration()));
            reportBuilder.append(String.format(Locale.ROOT, "Сумарна тривалість: %d хв%n", summary.totalDuration()));
            reportBuilder.append(String.format(Locale.ROOT, "Загальна потреба в ресурсах: %.2f од.%n", summary.totalResources()));
        } else {
            reportBuilder.append("Жодного коректного запису не знайдено.\n");
        }

        reportBuilder.append("\nЗнайдені помилки:\n");
        for (String error : errors) {
            reportBuilder.append(error).append("\n");
        }

        String finalReport = reportBuilder.toString();
        System.out.println(finalReport);

        try {
            Files.createDirectories(output.getParent());
            Files.writeString(output, finalReport, StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.out.println("Помилка запису файлу: " + e.getMessage());
        }
    }
}