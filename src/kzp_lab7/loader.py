import csv
from collections.abc import Iterable
from .models import Lesson, Lecture, Practice
from .errors import DataError

def build_lesson(row: dict[str, str], row_number: int) -> Lesson:
    """Перетворює CSV-рядок на відповідний підтип заняття."""
    try:
        common = {
            "subject": row["subject"].strip(),
            "teacher": row["teacher"].strip(),
            "day": row["day"].strip(),
            "period": int(row["period"]),
            "room": int(row["room"]),
        }
        kind = row.get("kind", "").strip()
        extra = int(row.get("extra", 0))

        if not common["subject"] or not common["teacher"] or not common["day"]:
            raise ValueError("Текстові поля не можуть бути порожніми")

        if kind == "lecture":
            return Lecture(**common, students=extra)
        if kind == "practice":
            return Practice(**common, computers=extra)
        
        raise ValueError(f"Невідомий тип заняття: {kind}")
    except ValueError as e:
        raise DataError(f"Помилка у рядку {row_number}: {e}")

def load_records(filepath: str) -> list[Lesson]:
    records = []
    try:
        with open(filepath, mode="r", encoding="utf-8") as file:
            reader = csv.DictReader(file)
            for i, row in enumerate(reader, start=2):
                records.append(build_lesson(row, i))
    except FileNotFoundError:
        raise DataError(f"Файл не знайдено: {filepath}")
    return records