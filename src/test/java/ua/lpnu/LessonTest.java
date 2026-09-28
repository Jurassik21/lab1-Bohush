package ua.lpnu;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import ua.lpnu.kzp.Lesson;
import ua.lpnu.kzp.RoomDuration;

class LessonTest {

    /* Перевіряє створення сутності з коректного CSV-рядка. */
    @Test
    void fromCsvCreatesValidEntity() {
        Lesson lesson = Lesson.fromCsv("Математика;Коваленко І.І.;Понеділок;115;120");
        assertEquals(115, lesson.getRoom());
        assertEquals(120, lesson.getDurationMinutes());
    }

    /* Перевіряє відхилення від'ємної аудиторії в конструкторі. */
    @Test
    void constructorRejectsNegativeRoom() {
        assertThrows(IllegalArgumentException.class,
            () -> new Lesson("Фізика", "Іваненко", "Вівторок", -5, 90));
    }

    /* Перевіряє відхилення некоректного числового формату у фабричному методі. */
    @Test
    void fromCsvRejectsInvalidNumber() {
        assertThrows(IllegalArgumentException.class,
            () -> Lesson.fromCsv("Математика;Коваленко І.І.;Понеділок;115;дві_години"));
    }

    /* Перевіряє рівність значень об'єктів record. */
    @Test
    void recordUsesValueEquality() {
        RoomDuration first = new RoomDuration(2, 115, 120, 210);
        RoomDuration second = new RoomDuration(2, 115, 120, 210);
        assertEquals(first, second);
    }
}
