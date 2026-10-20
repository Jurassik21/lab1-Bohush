package ua.lpnu.kzp;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class LessonTest {

    @Test
    void constructorRejectsNegativeRoom() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> new Lecture("Фізика", "Іваненко", "Вівторок", -5, 90));
        assertEquals("Номер аудиторії та тривалість мають бути додатними", exception.getMessage());
    }

    @Test
    void fromCsvRejectsInvalidNumber() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> Lesson.fromCsv("Математика;Коваленко І.І.;Понеділок;115;дві_години"));
        assertEquals("Числове поле має помилковий формат", exception.getMessage());
    }

    /* Перевіряє різну поведінку підтипів */
    @Test
    void subtypesCalculateResourceDifferently() {
        Lesson lecture = new Lecture("Математика", "Іваненко", "Пн", 215, 90);
        Lesson practice = new Practice("Математика", "Іваненко", "Пн", 115, 90);
        assertNotEquals(lecture.calculateResourceNeeds(), practice.calculateResourceNeeds());
    }

    /* Перевіряє відсутність дублювання логічно рівних об'єктів у множині */
    @Test
    void equalObjectsHaveOneSetEntry() {
        Set<Lesson> items = new HashSet<>();
        items.add(new Lecture("Математика", "Іваненко", "Пн", 215, 90));
        items.add(new Lecture("Математика", "Іваненко", "Пн", 215, 90));
        assertEquals(1, items.size());
    }
}