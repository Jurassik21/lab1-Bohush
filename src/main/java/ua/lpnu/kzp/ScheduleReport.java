package ua.lpnu.kzp;

import java.util.Comparator;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Набір чистих потокових запитів до розкладу занять (Варіант 3). */
public final class ScheduleReport {
    
    private ScheduleReport() {}

    /** 1. Відбір (filter): заняття у визначений день. */
    public static List<Lesson> lessonsOnDay(List<Lesson> lessons, String day) {
        Objects.requireNonNull(day, "День не може бути null");
        return streamOf(lessons)
                .filter(lesson -> lesson.getDay().equalsIgnoreCase(day))
                .toList();
    }

    /** 2. Перетворення (map): унікальні назви предметів. */
    public static List<String> subjectNames(List<Lesson> lessons) {
        return streamOf(lessons)
                .map(Lesson::getSubject)
                .distinct()
                .sorted()
                .toList();
    }

    /** 3. Групування (groupingBy): кількість занять за викладачем. */
    public static Map<String, Long> lessonsCountByTeacher(List<Lesson> lessons) {
        return streamOf(lessons)
                .collect(Collectors.groupingBy(
                        Lesson::getTeacher,
                        Collectors.counting()
                ));
    }

    /** 4. Статистика (summarizingInt): статистика тривалості занять. */
    public static IntSummaryStatistics durationStatistics(List<Lesson> lessons) {
        return streamOf(lessons)
                .collect(Collectors.summarizingInt(Lesson::getDurationMinutes));
    }

    /** 5. Top-5: за тривалістю (спадання), потім за назвою предмета (зростання). */
    public static List<Lesson> top5Lessons(List<Lesson> lessons) {
        Comparator<Lesson> byDurationAndSubject = Comparator
                .comparingInt(Lesson::getDurationMinutes)
                .reversed()
                .thenComparing(Lesson::getSubject);

        return streamOf(lessons)
                .sorted(byDurationAndSubject)
                .limit(5)
                .toList();
    }

    /** 6. Пошук (Optional): перше заняття у заданій аудиторії. */
    public static Optional<Lesson> findByRoom(List<Lesson> lessons, int room) {
        return streamOf(lessons)
                .filter(lesson -> lesson.getRoom() == room)
                .findFirst();
    }

    /* Допоміжний метод для перевірки списку на null перед створенням потоку. */
    private static Stream<Lesson> streamOf(List<Lesson> lessons) {
        return Objects.requireNonNull(lessons, "Список не може бути null").stream();
    }
}
