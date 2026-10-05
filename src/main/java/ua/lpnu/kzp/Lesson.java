package ua.lpnu.kzp;

import java.util.Objects;

/** Спільний абстрактний тип для занять у розкладі. */
public abstract class Lesson {
    private final String subject;
    private final String teacher;
    private final String day;
    private final int room;
    private final int durationMinutes;
    private final LessonKind kind;

    protected Lesson(String subject, String teacher, String day, int room, int durationMinutes, LessonKind kind) {
        this.subject = Objects.requireNonNull(subject, "Предмет не може бути null");
        this.teacher = Objects.requireNonNull(teacher, "Викладач не може бути null");
        this.day = Objects.requireNonNull(day, "День не може бути null");
        this.kind = Objects.requireNonNull(kind, "Категорія не може бути null");

        if (this.subject.isBlank() || this.teacher.isBlank() || this.day.isBlank()) {
            throw new IllegalArgumentException("Текстові поля не можуть бути порожніми");
        }
        if (room <= 0 || durationMinutes <= 0) {
            throw new IllegalArgumentException("Номер аудиторії та тривалість мають бути додатними");
        }

        this.room = room;
        this.durationMinutes = durationMinutes;
    }

    public static Lesson fromCsv(String line) {
        String[] fields = line.split(";", -1);
        if (fields.length != 5) {
            throw new IllegalArgumentException("Рядок повинен містити рівно 5 полів");
        }

        try {
            String subj = fields[0].trim();
            String teach = fields[1].trim();
            String day = fields[2].trim();
            int r = Integer.parseInt(fields[3].trim());
            int dur = Integer.parseInt(fields[4].trim());

            if (r >= 200) {
                return new Lecture(subj, teach, day, r, dur);
            } else {
                return new Practice(subj, teach, day, r, dur);
            }
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Числове поле має помилковий формат");
        }
    }

    public final String getSubject() { return subject; }
    public final String getTeacher() { return teacher; }
    public final String getDay() { return day; }
    public final int getRoom() { return room; }
    public final int getDurationMinutes() { return durationMinutes; }
    public final LessonKind getKind() { return kind; }

    /** Поліморфна операція: обчислює потребу в ресурсах. */
    public abstract double calculateResourceNeeds();

    @Override
    public final boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Lesson lesson = (Lesson) other;
        return subject.equals(lesson.subject) &&
               day.equals(lesson.day) &&
               teacher.equals(lesson.teacher);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(getClass(), subject, day, teacher);
    }
}