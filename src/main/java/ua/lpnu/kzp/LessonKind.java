package ua.lpnu.kzp;

/** Категорія заняття. */
public enum LessonKind {
    LECTURE("Лекція"),
    PRACTICE("Практика");

    private final String label;

    LessonKind(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}