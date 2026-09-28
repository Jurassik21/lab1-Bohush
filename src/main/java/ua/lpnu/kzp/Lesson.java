package ua.lpnu.kzp;

import java.util.Locale;
import java.util.Objects;

/**
 * Описує коректний запис заняття у розкладі.
 */
public final class Lesson {
    private final String subject;
    private final String teacher;
    private final String day;
    private final int room;
    private final int durationMinutes;

    /**
     * Створює коректний запис заняття після перевірки інваріантів.
     */
    public Lesson(String subject, String teacher, String day, int room, int durationMinutes) {
        // Перше і єдине присвоєння текстових полів
        this.subject = Objects.requireNonNull(subject, "Предмет не може бути null");
        this.teacher = Objects.requireNonNull(teacher, "Викладач не може бути null");
        this.day = Objects.requireNonNull(day, "Тип заняття/День не може бути null");

        if (subject.isBlank() || teacher.isBlank() || day.isBlank()) {
            throw new IllegalArgumentException("Текстові поля не можуть бути порожніми");
        }
        if (room <= 0 || durationMinutes <= 0) {
            throw new IllegalArgumentException("Номер аудиторії та тривалість мають бути додатними");
        }

        // Присвоєння числових полів
        this.room = room;
        this.durationMinutes = durationMinutes;
    }

    /**
     * Створює об'єкт заняття з одного CSV-рядка.
     */
    public static Lesson fromCsv(String line) {
        Objects.requireNonNull(line, "Рядок не може бути null");
        String[] fields = line.split(";", -1);
        
        if (fields.length != 5) {
            throw new IllegalArgumentException("Очікується 5 полів");
        }
        
        try {
            return new Lesson(
                fields[0].trim(),
                fields[1].trim(),
                fields[2].trim(),
                Integer.parseInt(fields[3].trim()),
                Integer.parseInt(fields[4].trim())
            );
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Числове поле має помилковий формат", exception);
        }
    }

    public int getRoom() { return room; }
    public int getDurationMinutes() { return durationMinutes; }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "%s (%s, %s): ауд. %d, %d хв",
            subject, teacher, day, room, durationMinutes);
    }
}