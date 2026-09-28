package ua.lpnu.kzp;

/** Зберігає незмінні підсумкові показники розкладу. */
public record RoomDuration(int validCount, int minRoom, int maxDuration, int totalDuration) {
    
    /** Перевіряє допустимість підсумкових показників. */
    public RoomDuration {
        if (validCount < 0 || minRoom < 0 || maxDuration < 0 || totalDuration < 0) {
            throw new IllegalArgumentException("Показники не можуть бути від'ємними");
        }
    }
}
