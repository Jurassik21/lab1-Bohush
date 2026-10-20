package ua.lpnu.kzp;

import java.util.List;
import java.util.Objects;

/** Спільний абстрактний тип для занять у розкладі. */
public abstract class Lesson {
    @CsvColumn("Предмет")
    private final String subject;
    @CsvColumn("Викладач")
    private final String teacher;
    @CsvColumn("День")
    private final String day;
    @CsvColumn("Аудиторія")
    private final int room;
    @CsvColumn("Тривалість")
    private final int durationMinutes;
    @CsvColumn("Категорія")
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

    /**
     * Захист від Finalizer Attack для SpotBugs (CT_CONSTRUCTOR_THROW).
     * Забороняє підкласам перевизначати фіналізатор у разі винятку в конструкторі.
     */
    @Override
    @SuppressWarnings("deprecation")
    protected final void finalize() {
        // Порожня реалізація для блокування уразливості
    }

    // Зберігаємо старий парсер для початкового вхідного файлу input.csv
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

    /** Створює запис із полів, розібраних новим експортером. */
    public static Lesson fromExportedFields(List<String> fields) throws DataStorageException {
        if (fields.size() != 6) {
            throw new DataStorageException("Очікується шість полів CSV");
        }
        try {
            String day = fields.get(0);
            int dur = Integer.parseInt(fields.get(1));
            LessonKind kind = LessonKind.valueOf(fields.get(2));
            int r = Integer.parseInt(fields.get(3));
            String subj = fields.get(4);
            String teach = fields.get(5);

            if (kind == LessonKind.LECTURE) {
                return new Lecture(subj, teach, day, r, dur);
            } else {
                return new Practice(subj, teach, day, r, dur);
            }
        } catch (IllegalArgumentException exception) {
            throw new DataStorageException("Некоректний рядок експортованого заняття", exception);
        }
    }

    public final String getSubject() { return subject; }
    public final String getTeacher() { return teacher; }
    public final String getDay() { return day; }
    public final int getRoom() { return room; }
    public final int getDurationMinutes() { return durationMinutes; }
    public final LessonKind getKind() { return kind; }

    public abstract double calculateResourceNeeds();

    @Override
    public final boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Lesson lesson = (Lesson) other;
        return durationMinutes == lesson.durationMinutes &&
               room == lesson.room &&
               subject.equals(lesson.subject) &&
               day.equals(lesson.day) &&
               teacher.equals(lesson.teacher) &&
               kind == lesson.kind;
    }

    @Override
    public final int hashCode() {
        return Objects.hash(getClass(), subject, teacher, day, room, durationMinutes, kind);
    }
}