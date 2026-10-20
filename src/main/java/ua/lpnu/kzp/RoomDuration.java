package ua.lpnu.kzp;

public record RoomDuration(int validCount, int minRoom, int maxDuration, int totalDuration, double totalResources, int oddRoomDuration) {
    public RoomDuration {
        if (validCount < 0 || minRoom < 0 || maxDuration < 0 || totalDuration < 0 || totalResources < 0 || oddRoomDuration < 0) {
            throw new IllegalArgumentException("Показники не можуть бути від'ємними");
        }
    }
}