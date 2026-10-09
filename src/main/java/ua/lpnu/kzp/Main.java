package ua.lpnu.kzp;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

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
        } catch (IOException e) {
            System.out.println("Помилка читання файлу: " + e.getMessage());
            return;
        }

        // 1. Фаза читання: лише парсинг і валідація
        List<Lesson> lessons = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        
        for (int index = 0; index < lines.size(); index++) {
            try {
                lessons.add(Lesson.fromCsv(lines.get(index)));
            } catch (IllegalArgumentException exception) {
                errors.add(String.format(Locale.ROOT, "Рядок %d: %s", index + 1, exception.getMessage()));
            }
        }

        // 2. Фаза розрахунку СТАРИХ показників через Stream API
        int validCount = lessons.size();
        int minRoom = lessons.stream().mapToInt(Lesson::getRoom).min().orElse(0);
        int maxDuration = lessons.stream().mapToInt(Lesson::getDurationMinutes).max().orElse(0);
        int totalDuration = lessons.stream().mapToInt(Lesson::getDurationMinutes).sum();
        double totalResources = lessons.stream().mapToDouble(Lesson::calculateResourceNeeds).sum();
        int oddRoomDuration = lessons.stream()
                .filter(l -> l.getRoom() % 2 != 0)
                .mapToInt(Lesson::getDurationMinutes).sum();

        RoomDuration summary = new RoomDuration(validCount, minRoom, maxDuration, totalDuration, totalResources, oddRoomDuration);

        // 3. Фаза розрахунку НОВИХ запитів (Лаб 4)
        String targetDay = "Понеділок"; // Константа для пошуку
        int targetRoom = 114;           // Константа для Optional

        List<Lesson> lessonsOnDay = ScheduleReport.lessonsOnDay(lessons, targetDay);
        List<String> subjectNames = ScheduleReport.subjectNames(lessons);
        Map<String, Long> teacherStats = ScheduleReport.lessonsCountByTeacher(lessons);
        List<Lesson> top5 = ScheduleReport.top5Lessons(lessons);
        Optional<Lesson> foundRoom = ScheduleReport.findByRoom(lessons, targetRoom);

        // 4. Генерація звіту
        StringBuilder reportBuilder = new StringBuilder();
        if (summary.validCount() > 0) {
            reportBuilder.append("=== СТАРІ ПОКАЗНИКИ ===\n");
            reportBuilder.append(String.format(Locale.ROOT, "Коректних записів: %d%n", summary.validCount()));
            reportBuilder.append(String.format(Locale.ROOT, "Найменший номер аудиторії: %d%n", summary.minRoom()));
            reportBuilder.append(String.format(Locale.ROOT, "Найдовше заняття: %d хв%n", summary.maxDuration()));
            reportBuilder.append(String.format(Locale.ROOT, "Сумарна тривалість: %d хв%n", summary.totalDuration()));
            reportBuilder.append(String.format(Locale.ROOT, "Загальна потреба в ресурсах: %.2f од.%n", summary.totalResources()));
            reportBuilder.append(String.format(Locale.ROOT, "Тривалість у непарних аудиторіях: %d хв%n", summary.oddRoomDuration()));

            reportBuilder.append("\n=== НОВІ ЗАПИТИ (Stream API) ===\n");
            reportBuilder.append(String.format(Locale.ROOT, "Заняття у день '%s': %d шт.%n", targetDay, lessonsOnDay.size()));
            reportBuilder.append(String.format(Locale.ROOT, "Унікальні предмети: %s%n", subjectNames));
            reportBuilder.append(String.format(Locale.ROOT, "Кількість занять за викладачами: %s%n", teacherStats));
            reportBuilder.append(String.format(Locale.ROOT, "Статистика тривалості (хв): %s%n", ScheduleReport.durationStatistics(lessons)));
            
            reportBuilder.append("Топ-5 найдовших занять:\n");
            top5.forEach(l -> reportBuilder.append(String.format(Locale.ROOT, " - %s (%d хв)%n", l.getSubject(), l.getDurationMinutes())));
            
            reportBuilder.append(String.format(Locale.ROOT, "Пошук аудиторії %d: %s%n", 
                    targetRoom, foundRoom.map(Lesson::getSubject).orElse("Не знайдено")));
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
        } catch (IOException e) {
            System.out.println("Помилка запису файлу: " + e.getMessage());
        }
    }
}