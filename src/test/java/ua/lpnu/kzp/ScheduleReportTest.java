package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ScheduleReportTest {

    private final List<Lesson> lessons = List.of(
            new Lecture("Математика", "Петренко", "Понеділок", 201, 90),
            new Practice("Програмування", "Іваненко", "Вівторок", 101, 120),
            new Practice("Математика", "Петренко", "Понеділок", 102, 90),
            new Lecture("Фізика", "Сидоренко", "Середа", 215, 60),
            new Practice("Бази даних", "Коваленко", "Четвер", 114, 120),
            new Lecture("Програмування", "Іваненко", "Вівторок", 202, 90)
    );

    @Test
    void filtersLessonsByDay() {
        List<Lesson> monday = ScheduleReport.lessonsOnDay(lessons, "Понеділок");
        assertEquals(2, monday.size());
    }

    @Test
    void mapsToUniqueSubjectNames() {
        List<String> subjects = ScheduleReport.subjectNames(lessons);
        assertEquals(List.of("Бази даних", "Математика", "Програмування", "Фізика"), subjects);
    }

    @Test
    void groupsCountByTeacher() {
        var grouped = ScheduleReport.lessonsCountByTeacher(lessons);
        assertEquals(2L, grouped.get("Петренко"));
        assertEquals(1L, grouped.get("Коваленко"));
    }

    @Test
    void calculatesDurationStatistics() {
        var stats = ScheduleReport.durationStatistics(lessons);
        assertEquals(6, stats.getCount());
        assertEquals(60, stats.getMin());
        assertEquals(120, stats.getMax());
    }

    @Test
    void ordersTop5Lessons() {
        List<Lesson> top = ScheduleReport.top5Lessons(lessons);
        assertEquals(5, top.size());
        // Перші два мають бути по 120 хв, впорядковані за алфавітом предмета
        assertEquals("Бази даних", top.get(0).getSubject());
        assertEquals("Програмування", top.get(1).getSubject());
    }

    @Test
    void findsRoomOrReturnsEmptyOptional() {
        Optional<Lesson> found = ScheduleReport.findByRoom(lessons, 114);
        assertTrue(found.isPresent());
        assertEquals("Бази даних", found.get().getSubject());

        Optional<Lesson> notFound = ScheduleReport.findByRoom(lessons, 999);
        assertTrue(notFound.isEmpty());
    }
}