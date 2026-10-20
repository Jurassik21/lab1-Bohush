package ua.lpnu.kzp;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Locale;

public final class Main {
    private Main() {}

    public static void main(String[] args) {
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, StandardCharsets.UTF_8));
        
        Path input = Path.of("data", "input.csv");
        Path exportPath = Path.of("target", "schedule-export.csv");
        Path reportPath = Path.of("out", "report.txt");

        StringBuilder report = new StringBuilder();

        List<String> lines;
        try {
            lines = Files.readAllLines(input, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("Помилка читання файлу: " + e.getMessage());
            return;
        }

        Repository<Lesson> repository = new Repository<>();
        List<String> errors = new ArrayList<>();

        for (int index = 0; index < lines.size(); index++) {
            try {
                repository.add(Lesson.fromCsv(lines.get(index)));
            } catch (IllegalArgumentException exception) {
                errors.add(String.format(Locale.ROOT, "Рядок %d: %s", index + 1, exception.getMessage()));
            }
        }

        List<Lesson> lessons = repository.all();

        // === 1. СТАРІ ПОКАЗНИКИ ТА НОВІ ЗАПИТИ (З ЛАБ 4) ===
        report.append("=== СТАРІ ПОКАЗНИКИ ===").append(System.lineSeparator());
        report.append(String.format(Locale.ROOT, "Коректних записів: %d%n", lessons.size()));
        
        int minRoom = lessons.stream().mapToInt(Lesson::getRoom).min().orElse(0);
        int maxDuration = lessons.stream().mapToInt(Lesson::getDurationMinutes).max().orElse(0);
        int totalDuration = lessons.stream().mapToInt(Lesson::getDurationMinutes).sum();
        double totalResources = lessons.stream().mapToDouble(Lesson::calculateResourceNeeds).sum();
        int oddRoomDuration = lessons.stream().filter(l -> l.getRoom() % 2 != 0).mapToInt(Lesson::getDurationMinutes).sum();

        report.append(String.format(Locale.ROOT, "Найменший номер аудиторії: %d%n", minRoom));
        report.append(String.format(Locale.ROOT, "Найдовше заняття: %d хв%n", maxDuration));
        report.append(String.format(Locale.ROOT, "Сумарна тривалість: %d хв%n", totalDuration));
        report.append(String.format(Locale.ROOT, "Загальна потреба в ресурсах: %.2f од.%n", totalResources));
        report.append(String.format(Locale.ROOT, "Тривалість у непарних аудиторіях: %d хв%n", oddRoomDuration));

        report.append(System.lineSeparator()).append("=== НОВІ ЗАПИТИ (Stream API) ===").append(System.lineSeparator());
        report.append(String.format(Locale.ROOT, "Заняття у день 'Понеділок': %d шт.%n", ScheduleReport.lessonsOnDay(lessons, "Понеділок").size()));
        report.append(String.format(Locale.ROOT, "Унікальні предмети: %s%n", ScheduleReport.subjectNames(lessons)));
        report.append(String.format(Locale.ROOT, "Кількість занять за викладачами: %s%n", ScheduleReport.lessonsCountByTeacher(lessons)));
        
        IntSummaryStatistics stats = ScheduleReport.durationStatistics(lessons);
        report.append(String.format(Locale.ROOT, "Статистика тривалості (хв): %s%n", stats));

        report.append("Топ-5 найдовших занять:").append(System.lineSeparator());
        for (Lesson l : ScheduleReport.top5Lessons(lessons)) {
            report.append(String.format(Locale.ROOT, " - %s (%d хв)%n", l.getSubject(), l.getDurationMinutes()));
        }

        String room114Subject = ScheduleReport.findByRoom(lessons, 114)
                .map(Lesson::getSubject)
                .orElse("Не знайдено");
        report.append(String.format(Locale.ROOT, "Пошук аудиторії 114: %s%n", room114Subject));

        // === 2. ЕКСПОРТ ТА ROUND-TRIP (З ЛАБ 5) ===
        report.append(System.lineSeparator()).append("=== ЗБЕРЕЖЕННЯ ТА ІМПОРТ (Лабораторна №5) ===").append(System.lineSeparator());
        try {
            // Експорт у CSV за допомогою рефлексії
            CsvExporter.write(exportPath, Lesson.class, repository.all());

            // Зчитування записаного файлу назад
            String csvText = Files.readString(exportPath, StandardCharsets.UTF_8);
            List<List<String>> rows = CsvParser.parseDocument(csvText);

            List<Lesson> restored = rows.stream()
                    .skip(1) // Пропускаємо заголовок
                    .map(fields -> {
                        try {
                            return Lesson.fromExportedFields(fields);
                        } catch (DataStorageException exception) {
                            throw new IllegalStateException(exception);
                        }
                    })
                    .toList();

            report.append(String.format(Locale.ROOT, "Записів у сховищі: %d%n", repository.all().size()));
            report.append(String.format(Locale.ROOT, "CSV-записів із заголовком: %d%n", rows.size()));
            report.append(String.format(Locale.ROOT, "Відновлених об'єктів: %d%n", restored.size()));
            report.append(String.format(Locale.ROOT, "Заголовок CSV: %s%n", String.join(",", rows.get(0))));
            report.append(String.format(Locale.ROOT, "Round-trip успішний (списки рівні): %s%n", repository.all().equals(restored)));

        } catch (DataStorageException | IOException | IllegalStateException exception) {
            report.append("Помилка під час експорту/імпорту: ").append(exception.getMessage()).append(System.lineSeparator());
            if (exception.getCause() != null) {
                report.append("Причина: ").append(exception.getCause().getMessage()).append(System.lineSeparator());
            }
        }

        // === 3. ПОМИЛКИ ВХІДНОГО ФАЙЛУ ===
        if (!errors.isEmpty()) {
            report.append(System.lineSeparator()).append("Знайдені помилки:").append(System.lineSeparator());
            for (String error : errors) {
                report.append(error).append(System.lineSeparator());
            }
        }

        String fullReport = report.toString();

        // 1. Виведення в консоль
        System.out.print(fullReport);

        // 2. Запис у файл out/report.txt
        try {
            Path parent = reportPath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.writeString(reportPath, fullReport, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
        } catch (IOException exception) {
            System.out.println("Не вдалося записати файл out/report.txt: " + exception.getMessage());
        }
    }
}