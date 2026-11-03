from dataclasses import dataclass
from typing import Protocol

class RoomNeeded(Protocol):
    """Описує протокол об'єктів із методом потреби в аудиторії."""
    def room_need(self) -> int:
        ...

class Lesson:
    """Базове заняття зі спільним станом."""
    def __init__(self, subject: str, teacher: str, day: str, period: int, room: int) -> None:
        self.subject = subject
        self.teacher = teacher
        self.day = day
        self.period = period
        self.room = room

    @property
    def room(self) -> int:
        return self._room

    @room.setter
    def room(self, value: int) -> None:
        if value <= 0:
            raise ValueError("Аудиторія має бути додатним числом")
        self._room = value

    @property
    def period(self) -> int:
        return self._period

    @period.setter
    def period(self, value: int) -> None:
        if value <= 0:
            raise ValueError("Пара має бути додатним числом")
        self._period = value

    def room_need(self) -> int:
        raise NotImplementedError

    def __str__(self) -> str:
        return f"{self.subject} ({self.day}, пара {self.period}, ауд. {self.room})"

    def __repr__(self) -> str:
        return (f"{type(self).__name__}(subject={self.subject!r}, teacher={self.teacher!r}, "
                f"day={self.day!r}, period={self.period!r}, room={self.room!r})")

    def __eq__(self, other: object) -> bool:
        if not isinstance(other, Lesson):
            return NotImplemented
        return type(self) is type(other) and self.__dict__ == other.__dict__

class Lecture(Lesson):
    """Підтип: Лекція."""
    def __init__(self, subject: str, teacher: str, day: str, period: int, room: int, students: int) -> None:
        super().__init__(subject, teacher, day, period, room)
        if students <= 0:
            raise ValueError("Кількість студентів має бути додатною")
        self.students = students

    def room_need(self) -> int:
        return self.students

class Practice(Lesson):
    """Підтип: Практика."""
    def __init__(self, subject: str, teacher: str, day: str, period: int, room: int, computers: int) -> None:
        super().__init__(subject, teacher, day, period, room)
        if computers <= 0:
            raise ValueError("Кількість комп'ютерів має бути додатною")
        self.computers = computers

    def room_need(self) -> int:
        return self.computers * 2

@dataclass(frozen=True)
class ScheduleSummary:
    """Зберігає незмінний підсумок аналізу розкладу."""
    day: str
    count: int
    first_period: int
    last_period: int
    rooms: list[int]
    teacher_counts: dict[str, int]
    total_room_need: int