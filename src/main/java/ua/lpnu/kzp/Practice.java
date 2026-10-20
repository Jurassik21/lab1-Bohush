package ua.lpnu.kzp;

/** Практичне заняття. */
public final class Practice extends Lesson {
    public Practice(String subject, String teacher, String day, int room, int durationMinutes) {
        super(subject, teacher, day, room, durationMinutes, LessonKind.PRACTICE);
    }

    @Override
    public double calculateResourceNeeds() {
        return getDurationMinutes() * 3.0;
    }
}