package ua.lpnu.kzp;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/** Описує типобезпечне сховище об'єктів у пам'яті. */
public final class Repository<T> {
    private final List<T> items = new ArrayList<>();

    /** Додає непорожній запис до сховища. */
    public void add(T item) {
        items.add(Objects.requireNonNull(item, "Запис не може бути null"));
    }

    /** Повертає незмінний знімок усіх записів. */
    public List<T> all() {
        return List.copyOf(items);
    }

    /** Повертає записи, що задовольняють умову. */
    public List<T> find(Predicate<? super T> condition) {
        Objects.requireNonNull(condition, "Умова не може бути null");
        return items.stream()
                .filter(condition)
                .toList();
    }
}