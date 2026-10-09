package ua.lpnu.kzp;

/** Лекційне заняття. */
public final class Lecture extends Lesson {
    public Lecture(String subject, String teacher, String day, int room, int durationMinutes) {
        super(subject, teacher, day, room, durationMinutes, LessonKind.LECTURE);
    }

    @Override
    public double calculateResourceNeeds() {
        return getDurationMinutes() * 1.5;
    }
}